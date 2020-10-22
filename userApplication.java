/*
 *
 * Computer Networks 2
 *
 * Experimental Virtual Lab
 *
 * Java network Socket Programming
 *
 * Author :Michael Karatzas
 * AEM:9137
 *
 */


import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class userApplication{

    //Virtual Connection parameters
    static String client_public_address ="87.202.99.120\r";
    static byte[] hostIP = { (byte)155,(byte)207,(byte)18,(byte)208 };
    static int client_listening_port=   48027   ;
    static int server_listening_port=   38027   ;

    //Request codes
    static String echo_request_code="E7655\r";
    static String echo_request_code_no_delay="E0000\r";
    static String image_request_code="M6183";
    static String audio_request_code="A5594\r";
    static String ithakicopter_code="Q4330\r";
    static String vehicle_obd_II_code="V0110\r";

    //Filestreams declaration for files, where programm's data is going to be stored.
    static FileOutputStream console_full;
    static FileOutputStream echoPacketsTimes_delay;
    static FileOutputStream echoPacketsTimes_NoDelay;
    static FileOutputStream image_cam1;
    static FileOutputStream image_cam2;

    public static void main(String[] param) throws SocketException,IOException {

        //In console_full.csv File we print all the readable output of our program execution.
        console_full=new FileOutputStream("console_full.csv");
        System.out.println("!!!!!!!!!  JAVA SOCKET PROGRAM HAS STARTED  !!!!!!!!!");
        console_full.write("!!!!!!!!!  JAVA SOCKET PROGRAM HAS STARTED  !!!!!!!!!\n".getBytes());

        //Get echo packets Response Times for more than 4 minutes - with delay
        //echoPacketsTimes(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");

        //Get echo packets Response Times for more than 4 minutes - without delay
        //echoPacketsTimes(echo_request_code_no_delay,echoPacketsTimes_NoDelay, "echoPacketsResTimes_NoDelay");


        getImage( image_request_code+"CAM=PTZ\r", image_cam1, "img_CAM1");

        console_full.close();
    }



    //Function to get the times of echopackets
    private static void echoPacketsTimes( String code, FileOutputStream resTimes_file, String responseTimesFilename) throws IOException {

        //Print message to indicate the beginning of echo packets interchange
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print("Echo Packets START ");
        System.out.println(formatter.format(date));
        System.out.println("Echo Packets:");

        //In consolte.txt is written all the readable output
        console_full.write("Echo Packets START ".getBytes());
        console_full.write(formatter.format(date).getBytes());
        console_full.write("\nEcho Packets:\n".getBytes());

        //Variable to store the start timestamp of the 4+ minutes echo packets exchange
        long startTime=0;

        //Variable to store the start timestamp of an echo serverResponse packet
        long packetStartTime=0;
        //Variable to store the end timestamp of an echo serverResponse packet
        long packetEndTime=0;

        //opening a stream to the file
        resTimes_file=new FileOutputStream( responseTimesFilename + ".csv");
        //Datagram Socket for sending request packets initialization.
        DatagramSocket s = new DatagramSocket();
        byte[] txbuffer = code.getBytes();
        InetAddress hostAddress = InetAddress.getByAddress(hostIP);
        DatagramPacket clientRequest = new DatagramPacket(txbuffer,txbuffer.length,
                hostAddress,server_listening_port);


        DatagramSocket r = new DatagramSocket(client_listening_port);
        r.setSoTimeout(8000);
        byte[] rxbuffer = new byte[2048];
        DatagramPacket serverResponse = new DatagramPacket(rxbuffer,rxbuffer.length);


        startTime=System.currentTimeMillis();
        // We want our echo packets interchange to long for 4 minutes at least - 4 minutes is equal to 240000 milliseconds
        // So by executing our echo packets interchange for 255000 milliseconds, it lasts for 4 minutes and 15 seconds.
        while (packetEndTime-startTime< 25000) {
            s.send(clientRequest);
            //The timestamp when we sent the clientRequest packet, is the start of the server response time.
            packetStartTime=System.currentTimeMillis();

                try {
                    r.receive(serverResponse);
                    //The timestamp when we get the serverResponse packet, is the end of the server response time.
                    packetEndTime=System.currentTimeMillis();
                    String message = new String(rxbuffer,0,serverResponse.getLength());
                    System.out.println("The length of a package = " + serverResponse.getLength() + "bytes");
                    resTimes_file.write((  (packetEndTime-packetStartTime ) +"\n"   ).getBytes());
                    System.out.println(message);
                    console_full.write((message+"\n").getBytes());
                } catch (Exception x) {
                    System.out.println(x);
                }

        }

        //closing the UDP sockets opened before
        s.close();
        r.close();
        resTimes_file.close();


        //Print message to indicate the end of echo packets interchange
        System.out.print("Echo Packets END ");
        console_full.write("Echo Packets END ".getBytes());
        System.out.print(formatter.format(date));
        console_full.write(formatter.format(date).getBytes());
        System.out.print(" \n \n \n ");
        console_full.write(" \n \n \n ".getBytes());
    }


    //Function to get the times of echopackets
    private static void getImage( String code, FileOutputStream img_file, String imgFilename) throws IOException {

        //Print message to indicate the beginning of echo packets interchange
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print("Image Download START ");
        System.out.println(formatter.format(date));

        //In consolte.txt is written all the readable output
        console_full.write("Image Download START ".getBytes());
        console_full.write(formatter.format(date).getBytes());



        //opening a stream to the file
        img_file=new FileOutputStream( imgFilename + ".jpg");
        //Datagram Socket for sending request packets initialization.
        DatagramSocket s = new DatagramSocket();
        byte[] txbuffer = code.getBytes();
        InetAddress hostAddress = InetAddress.getByAddress(hostIP);
        DatagramPacket clientRequest = new DatagramPacket(txbuffer,txbuffer.length,
                hostAddress,server_listening_port);


        DatagramSocket r = new DatagramSocket(client_listening_port);
        r.setSoTimeout(8000);
        byte[] rxbuffer = new byte[2048];
        DatagramPacket serverResponse = new DatagramPacket(rxbuffer,rxbuffer.length);

        s.send(clientRequest);
        int L = 128;
        do {
            try {
                r.receive(serverResponse);
                byte[] datagramBytes = Arrays.copyOfRange(rxbuffer,0,serverResponse.getLength());
                img_file.write(datagramBytes);

            } catch (Exception x) {
                System.out.println(x);
            }

        }while (serverResponse.getLength() == L);

        //closing the UDP sockets opened before
        s.close();
        r.close();
        img_file.close();


        //Print message to indicate the end of echo packets interchange
        System.out.print("Image Download END ");
        console_full.write("Image Download END ".getBytes());
        System.out.print(formatter.format(date));
        console_full.write(formatter.format(date).getBytes());
        System.out.print(" \n \n \n ");
        console_full.write(" \n \n \n ".getBytes());
    }

//    //Function to get the times of echopackets
//    private static void echoPacketsThroughput( String code, FileOutputStream throughput_file,String filename) throws IOException {
//        throughput_file=new FileOutputStream(filename +".csv");
//    }



}
