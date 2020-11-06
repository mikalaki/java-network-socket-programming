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
    private static String client_public_address ="87.202.99.120";
    private static byte[] hostIP = { (byte)155,(byte)207,(byte)18,(byte)208 };
    private static int client_listening_port=   48032   ;
    private static int server_listening_port=   38032   ;

    //Request codes
    private static String echo_request_code="E8143";
    private static String echo_request_code_no_delay="E0000";
    private static String image_request_code="M3279";
    private static String audio_request_code="A4800\r";
    private static String ithakicopter_code="Q7966\r";
    private static String vehicle_obd_II_code="V3484\r";

    //Filestreams declaration for files, where programm's data is going to be stored.
    private static FileOutputStream console_full;
    private static FileOutputStream echoPacketsTimes_delay;
    private static FileOutputStream echoPacketsTimes_NoDelay;
    private static FileOutputStream image_cam;
    private static FileOutputStream temperatures;

    public static void main(String[] param) throws SocketException,IOException {

        //In console_full.csv File we print all the readable output of our program execution.
        console_full=new FileOutputStream("console_full.txt");
        System.out.println("!!!!!!!!!  JAVA SOCKET PROGRAM HAS STARTED  !!!!!!!!!");
        console_full.write("!!!!!!!!!  JAVA SOCKET PROGRAM HAS STARTED  !!!!!!!!!\n".getBytes());

        //Get echo packets Response Times for more than 4 minutes - with delay
        echoPacketsTimes(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");

        //Get echo packets Response Times for more than 4 minutes - without delay
        echoPacketsTimes(echo_request_code_no_delay,echoPacketsTimes_NoDelay, "echoPacketsResTimes_NoDelay");

        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
        //get the first image
        getImage(  image_cam, "img_CAM1", "CAM=FIX");

        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
        //get the second image
        getImage(  image_cam, "img_CAM2", "CAM=PTZ");

        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
        //get the temperatures request's response in the temperatures file
        getTemperatures(echo_request_code,temperatures, "temperatures");

        //get sound

        //closing filestream to console_full.txt
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
        byte[] txbuffer = (code).getBytes();
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
        while (packetEndTime-startTime< 255000) {
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
        System.out.print(" \n \n \n");
        console_full.write(" \n \n \n".getBytes());
    }

    //Function to get 5 echopackets before every other request  -- For the wireshark screenshots.
    private static void getSomeEchoPackets( String code, FileOutputStream resTimes_file, String responseTimesFilename) throws IOException {

        //Print message to indicate the beginning of echo packets interchange
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print("5 Echo Packets START ");
        System.out.println(formatter.format(date));
        System.out.println("Echo Packets:");

        //In consolte.txt is written all the readable output
        console_full.write("5 Echo Packets START ".getBytes());
        console_full.write(formatter.format(date).getBytes());
        console_full.write("\nEcho Packets:\n".getBytes());

//        //Variable to store the start timestamp of the 4+ minutes echo packets exchange
//        long startTime=0;
//
//        //Variable to store the start timestamp of an echo serverResponse packet
//        long packetStartTime=0;
//        //Variable to store the end timestamp of an echo serverResponse packet
//        long packetEndTime=0;

//        //opening a stream to the file
//        resTimes_file=new FileOutputStream( responseTimesFilename + ".csv");
        //Datagram Socket for sending request packets initialization.
        DatagramSocket s = new DatagramSocket();
        byte[] txbuffer = (code).getBytes();
        InetAddress hostAddress = InetAddress.getByAddress(hostIP);
        DatagramPacket clientRequest = new DatagramPacket(txbuffer,txbuffer.length,
                hostAddress,server_listening_port);


        DatagramSocket r = new DatagramSocket(client_listening_port);
        r.setSoTimeout(8000);
        byte[] rxbuffer = new byte[2048];
        DatagramPacket serverResponse = new DatagramPacket(rxbuffer,rxbuffer.length);


        // We want our echo packets interchange to long for 4 minutes at least - 4 minutes is equal to 240000 milliseconds
        // So by executing our echo packets interchange for 255000 milliseconds, it lasts for 4 minutes and 15 seconds.
        for(int i=0; i<6 ;i++) {
            s.send(clientRequest);
            //The timestamp when we sent the clientRequest packet, is the start of the server response time.
//            packetStartTime=System.currentTimeMillis();

            try {
                r.receive(serverResponse);
                //The timestamp when we get the serverResponse packet, is the end of the server response time.
//                packetEndTime=System.currentTimeMillis();
                String message = new String(rxbuffer,0,serverResponse.getLength());
//                System.out.println("The length of a package = " + serverResponse.getLength() + "bytes");
//                resTimes_file.write((  (packetEndTime-packetStartTime ) +"\n"   ).getBytes());
                System.out.println(message);
                console_full.write((message+"\n").getBytes());
            } catch (Exception x) {
                System.out.println(x);
            }

        }

        //closing the UDP sockets opened before
        s.close();
        r.close();
//        resTimes_file.close();


        //Print message to indicate the end of echo packets interchange
        System.out.print("5 Echo Packets END ");
        console_full.write("5 Echo Packets END ".getBytes());
        System.out.print(formatter.format(date));
        console_full.write(formatter.format(date).getBytes());
        System.out.print(" \n \n");
        console_full.write(" \n \n".getBytes());
    }


    /*Function to get the times of echopackets
    * String code : the echo_request_code
    *
    * */
    private static void getImage( FileOutputStream img_file, String imgFilename,String Image_params) throws IOException {

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

        //Creating the request string from the function input arguments.
        String request_string = image_request_code + Image_params ;
        byte[] txbuffer = request_string.getBytes();
        InetAddress hostAddress = InetAddress.getByAddress(hostIP);
        DatagramPacket clientRequest = new DatagramPacket(txbuffer,txbuffer.length,
                hostAddress,server_listening_port);

        DatagramSocket r = new DatagramSocket(client_listening_port);
        r.setSoTimeout(8000);
        //Set the size to 1024, because this is the biggest possible packet length (L) the servers sends.
        byte[] rxbuffer = new byte[1024];
        DatagramPacket serverResponse = new DatagramPacket(rxbuffer,rxbuffer.length);
        s.send(clientRequest);

        //The 2 final bytes of a datagram received from server indicates if we are at the end of our image (image end delimeter)
        byte[] datagramFinalTwoBytes = new byte[2];

        do {
            try {
                r.receive(serverResponse);
                byte[] datagramBytes = Arrays.copyOfRange(rxbuffer,0,serverResponse.getLength());
                img_file.write(datagramBytes);

                datagramFinalTwoBytes[0]=datagramBytes[datagramBytes.length-2];
                datagramFinalTwoBytes[1]=datagramBytes[datagramBytes.length-1];
            } catch (Exception x) {
                System.out.println(x);
            }
        //
        }while (! (datagramFinalTwoBytes[0] == (byte)(0xFF) && datagramFinalTwoBytes[1]== (byte)(0xD9)  ) );

        //closing the UDP sockets opened before
        s.close();
        r.close();
        img_file.close();


        //Print message to indicate the end of echo packets interchange
        System.out.print("Image Download END ");
        console_full.write("Image Download END ".getBytes());
        System.out.print(formatter.format(date));
        console_full.write(formatter.format(date).getBytes());
        System.out.print(" \n \n \n");
        console_full.write(" \n \n \n".getBytes());
    }

    //Function to get the times of echopackets
    private static void getTemperatures( String code, FileOutputStream temperatures_file, String temperaturesFilename) throws IOException {

        //Print message to indicate the beginning of echo packets interchange
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print("Temperatures Download START ");
        System.out.println(formatter.format(date));
        System.out.println("Temperatures:");

        //In consolte.txt is written all the readable output
        console_full.write("Temperatures Download START ".getBytes());
        console_full.write(formatter.format(date).getBytes());
        console_full.write("\nTemperatures:\n".getBytes());

        //opening a stream to the file
        temperatures_file=new FileOutputStream( temperaturesFilename + ".csv");
        //Datagram Socket for sending request packets initialization.
        DatagramSocket s = new DatagramSocket();

        //Datagram Socket for receiving response packets initialization.
        DatagramSocket r = new DatagramSocket(client_listening_port);

        for( int i =0 ;i< 100 ;i++) {

            String tempParam = String.format("%02d", i);
            byte[] txbuffer = (code+"T"+tempParam).getBytes();
            InetAddress hostAddress = InetAddress.getByAddress(hostIP);
            DatagramPacket clientRequest = new DatagramPacket(txbuffer,txbuffer.length,
                    hostAddress,server_listening_port);

            r.setSoTimeout(8000);
            byte[] rxbuffer = new byte[2048];
            DatagramPacket serverResponse = new DatagramPacket(rxbuffer,rxbuffer.length);

            s.send(clientRequest);
            //The timestamp when we sent the clientRequest packet, is the start of the server response time.


            try {
                r.receive(serverResponse);
                //The timestamp when we get the serverResponse packet, is the end of the server response time.
                String message = new String(rxbuffer,0,serverResponse.getLength());
                System.out.println("The length of a package = " + serverResponse.getLength() + "bytes");
                temperatures_file.write((  message +"\n"   ).getBytes());
                System.out.println(message);
                console_full.write((message+"\n").getBytes());
            } catch (Exception x) {
                System.out.println(x);
            }
        }

        //closing the UDP sockets opened before
        s.close();
        r.close();
        temperatures_file.close();

        //Print message to indicate the end of temperatures download
        System.out.print("Temperatures END ");
        console_full.write("Temperatures END ".getBytes());
        System.out.print(formatter.format(date));
        console_full.write(formatter.format(date).getBytes());
        System.out.print(" \n \n \n");
        console_full.write(" \n \n \n".getBytes());
    }

}
