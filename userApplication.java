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

public class userApplication{

    //Virtual Connection parameters
    static String client_public_address ="155.207.212.36\r";
    static byte[] hostIP = { (byte)155,(byte)207,(byte)18,(byte)208 };
    static int client_listening_port=   48016   ;
    static int server_listening_port=   38016   ;

    //Request codes
    static String echo_request_code="E5037\r";
    static String echo_request_code_no_delay="E0000\r";
    static String image_request_code="M6402\r";
    static String audio_request_code="A2536\r";
    static String ithakicopter_code="Q1648\r";
    static String vehicle_obd_II_code="V5910\r";

    //Filestreams declaration for files, where programm's data is going to be stored.
    static FileOutputStream console_full;
    static FileOutputStream echoPacketsTimes_delay;
    static FileOutputStream echoPacketsTimes_NoDelay;
    static FileOutputStream echosMAthroughput_delay;
    static FileOutputStream echosMAthroughput_NoDelay;

    public static void main(String[] param) throws SocketException,IOException {

        //In console_full.csv File we print all the outputs of our execution.
        console_full=new FileOutputStream("console_full.csv");
        System.out.println("!!!!!!!!!  JAVA SOCKET PROGRAM HAS STARTED  !!!!!!!!!");
        console_full.write("!!!!!!!!!  JAVA SOCKET PROGRAM HAS STARTED  !!!!!!!!!\n".getBytes());

        //Get echo packets Response Times for more than 4 minutes - with delay
        echoPacketsTimes(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");

        //Get echo packets Response Times for more than 4 minutes - without delay
        echoPacketsTimes(echo_request_code_no_delay,echoPacketsTimes_NoDelay, "echoPacketsResTimes_NoDelay");

        //
        // //Get echoPacket Throughput for 4 minutes (1 time with delay &1 without)
        // echoPacketsThroughput(echo_request_code,echosMAthroughput_delay,"echosMAthroughput_delay" );
        // echoPacketsThroughput(echo_request_code_no_delay,echosMAthroughput_NoDelay,"echosMAthroughput_NoDelay" );

    }



    //Function to get the times of echopackets
    private static void echoPacketsTimes( String code, FileOutputStream resTimes_file, String responseTimesFilename) throws IOException {


        System.out.println("Echo Packets START");
        System.out.println("Echo Packets:");
        console_full.write("Echo Packets START \n".getBytes());
        console_full.write("Echo Packets:\n".getBytes());

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
        byte[] txbuffer = echo_request_code.getBytes();
        InetAddress hostAddress = InetAddress.getByAddress(hostIP);
        DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length,
                hostAddress,server_listening_port);


        DatagramSocket r = new DatagramSocket(client_listening_port);
        r.setSoTimeout(8000);
        byte[] rxbuffer = new byte[2048];
        DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);


        startTime=System.currentTimeMillis();
        // We want our echo packets interchange to long for 4 minutes at least - 4 minutes is equal to 240000 milliseconds
        // So by executing our echo packets interchange for 255000 milliseconds, it lasts for 4 minutes and 15 seconds.
        while (packetEndTime-startTime< 10000) {
            s.send(p);
            //The timestamp when we sent the clientRequest packet, is the start of the server response time.
            packetStartTime=System.currentTimeMillis();

                try {
                    r.receive(q);
                    //The timestamp when we get the serverResponse packet, is the end of the server response time.
                    packetEndTime=System.currentTimeMillis();
                    String message = new String(rxbuffer,0,q.getLength());
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

        //messages for the end of this process
        System.out.println("Echo Packets END \n \n");
        console_full.write("Echo Packets END \n \n \n".getBytes());
    }

    //Function to get the times of echopackets
    private static void echoPacketsThroughput( String code, FileOutputStream throughput_file,String filename) throws IOException {
        throughput_file=new FileOutputStream(filename +".csv");
    }



}
