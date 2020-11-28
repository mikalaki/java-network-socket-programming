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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.sound.sampled.*;
import java.util.Arrays;




public class userApplication{

    //Virtual Connection parameters
    private static String client_public_address ="87.202.99.120";
    private static byte[] hostIP = { (byte)155,(byte)207,(byte)18,(byte)208 };
    private static int client_listening_port=   48042   ;
    private static int server_listening_port=   38042   ;



    //Request codes
    private static String echo_request_code="E6372";
    private static String echo_request_code_no_delay="E0000";
    private static String image_request_code="M2089";
    private static String audio_request_code="A9445";
    private static String ithakicopter_code="Q1121\r";
    private static String vehicle_obd_II_code="V6810\r";

    //Programm parameters
    private static int N_OF_SOUND_PACKETS = 900;

    //Filestreams declaration for files, where programm's data is going to be stored.
    private static FileOutputStream console_full;
    private static FileOutputStream echoPacketsTimes_delay;
    private static FileOutputStream echoPacketsTimes_NoDelay;
    private static FileOutputStream image_cam;
    private static FileOutputStream temperatures;
    private static FileOutputStream sound_samples_diff;
    private static FileOutputStream sound_samples;
    private static FileOutputStream sound_mean_value;
    private static FileOutputStream sound_steps;
    private static FileOutputStream telemetry;

    public static void main(String[] param) throws SocketException,IOException,LineUnavailableException {

        //In console_full.csv File we print all the readable output of our program execution.
        console_full=new FileOutputStream("console_full.txt");
        System.out.println("!!!!!!!!!  JAVA SOCKET PROGRAM HAS STARTED  !!!!!!!!!");
        console_full.write("!!!!!!!!!  JAVA SOCKET PROGRAM HAS STARTED  !!!!!!!!!\n".getBytes());

//        //Get echo packets Response Times for more than 4 minutes - with delay
//        echoPacketsTimes(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
//
//        //Get echo packets Response Times for more than 4 minutes - without delay
//        echoPacketsTimes(echo_request_code_no_delay,echoPacketsTimes_NoDelay, "echoPacketsResTimes_NoDelay");
//
//        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
//        //get the first image
//        getImage(  image_cam, "img_CAM1", "CAM=FIX");
//
//        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
//        //get the second image
//        getImage(  image_cam, "img_CAM2", "CAM=PTZ");
//
//        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
//        //get the temperatures request's response in the temperatures file
//        getTemperatures(echo_request_code,temperatures, "temperatures");
//
//
//        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
//        //get frequency generator samples from simple DPCM
//        getSoundfromSimpleDPCM("DPCM_freq_samples_diff","DPCM_freq_actual_samples","T" + String.valueOf(N_OF_SOUND_PACKETS));
//
//        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
//        //get song samples from simple DPCM
//        getSoundfromSimpleDPCM("DPCM_song_samples_diff","DPCM_song_actual_samples","F" + String.valueOf(N_OF_SOUND_PACKETS));
//
//        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
//        //get song samples from AQDPCM --- song1
//        getSoundfromAQDPCM("AQDPCM_song1_samples_diff","AQDPCM_song1_actual_samples","AQDPCM_song1_actual_meanValues",
//                "AQDPCM_song1_actual_steps","F" +"AQ" + String.valueOf(N_OF_SOUND_PACKETS));
//
//        getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
//        //get song samples from AQDPCM --- song2
//        getSoundfromAQDPCM("AQDPCM_song1_samples_diff","AQDPCM_song1_actual_samples","AQDPCM_song1_actual_meanValues",
//                "AQDPCM_song1_actual_steps","F" +"AQ" + String.valueOf(N_OF_SOUND_PACKETS));

        //getSomeEchoPackets(echo_request_code,echoPacketsTimes_delay, "echoPacketsResTimes_delay");
        //Ithakicopters telemetry
        IthakiCopter("Telemetry_LLL_RRR_AAA_TTTT_PPPPPP");

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
                    //Print the size of a package.
                    //System.out.println("The size of a package = " + serverResponse.getLength() + "bytes");
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

        // We want to get some echo packets before other requests.
        for(int i=0; i<6 ;i++) {
            s.send(clientRequest);
            //The timestamp when we sent the clientRequest packet, is the start of the server response time.
//            packetStartTime=System.currentTimeMillis();

            try {
                r.receive(serverResponse);
                //The timestamp when we get the serverResponse packet, is the end of the server response time.
//                packetEndTime=System.currentTimeMillis();
                String message = new String(rxbuffer,0,serverResponse.getLength());
//                System.out.println("The size of a package = " + serverResponse.getLength() + "bytes");
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


    //Function for getting the image files.
    private static void getImage( FileOutputStream img_file, String imgFilename,String Image_request_params) throws IOException {

        //Print message to indicate the beginning of echo packets interchange
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print("Image Download START ");
        System.out.println(formatter.format(date));
        System.out.print("\n");
        //In consolte.txt is written all the readable output
        console_full.write("Image Download START ".getBytes());
        console_full.write(formatter.format(date).getBytes());
        console_full.write("\n".getBytes());
        //opening a stream to the file
        img_file=new FileOutputStream( imgFilename + ".jpg");
        //Datagram Socket for sending request packets initialization.
        DatagramSocket s = new DatagramSocket();

        //Creating the request string from the function input arguments.
        String request_string = image_request_code + Image_request_params ;
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

    //Function to get the temperatures of the stations
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

    //Function for getting the sound samples.
    private static void getSoundfromSimpleDPCM(String samples_diff_filename, String samples_filename,String Sound_request_params) throws IOException, LineUnavailableException {

        //masks to get the nibbles
        int nibble1mask = 0b11110000;
        int nibble2mask = 0b00001111;

        //values of B (beta) and mean value (mean) in DPCM is 1 and 0
        int beta = 1 ;
        int mean_value= 0;


        //Print message to indicate the beginning of echo packets interchange
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print("Sound Sample Download START ");
        System.out.println(formatter.format(date));
        System.out.print("\n");
        //In consolte.txt is written all the readable output
        console_full.write("Sound Sample START ".getBytes());
        console_full.write(formatter.format(date).getBytes());
        console_full.write("\n".getBytes());

        //opening streams to the files
        sound_samples_diff = new FileOutputStream( samples_diff_filename + ".csv");
        sound_samples = new FileOutputStream( samples_filename + ".csv");

        //Datagram Socket for sending request packets initialization.
        DatagramSocket s = new DatagramSocket();

        //Creating the request string from the function input arguments.
        String request_string = audio_request_code + Sound_request_params;
        byte[] txbuffer = request_string.getBytes();
        InetAddress hostAddress = InetAddress.getByAddress(hostIP);
        DatagramPacket clientRequest = new DatagramPacket(txbuffer,txbuffer.length,
                hostAddress,server_listening_port);

        DatagramSocket r = new DatagramSocket(client_listening_port);
        r.setSoTimeout(8000);
        //Set the size to 128, because for simpple DPCM applications that's the length of the packets we reveice in bytes.
        byte[] rxbuffer = new byte[128];
        DatagramPacket serverResponse = new DatagramPacket(rxbuffer,rxbuffer.length);
        s.send(clientRequest);


        //ArrayList<Byte> ActualSamples_Bytes= new ArrayList<>(); // arraylists for store the actual 2 samples in bytes after demodulation
        ArrayList<Integer> ActualSamples_Int= new ArrayList<>(); // arraylists for store the actual 2 samples values after demodulation

        // the remaining number of sound packets
        int nOfPacketsRemaining = N_OF_SOUND_PACKETS;
        do {
            try {
                r.receive(serverResponse);
                for (int i = 0; i < 128; i++) {
                    int diff1, diff2, sample1;
                    int sample2=0;

                    // Getting the two difference between 2 samples from the two nibbles
                    diff1 =( ((nibble1mask & rxbuffer[i]) >> 4) - 8 ) * beta;
                    diff2 =( (nibble2mask & rxbuffer[i]) - 8) * beta;
                    sound_samples_diff.write((  (diff1) +"\n"   ).getBytes());
                    sound_samples_diff.write((  (diff2) +"\n"   ).getBytes());


                    //getting the two samples values
                    sample1 = diff1 + sample2;
                    sample2 = diff2 + sample1;

                    sound_samples.write((  (sample1) +"\n"   ).getBytes());
                    sound_samples.write((  (sample2) +"\n"   ).getBytes());

                    //storring values of 2 samples difference in order
                    ActualSamples_Int.add(sample1);
                    ActualSamples_Int.add(sample2);
                }




            } catch (Exception x) {
                System.out.println(x);
            }

            //Decrease the number of remaining packets.
            nOfPacketsRemaining--;
        }while (nOfPacketsRemaining > 0);


        //Getting our clip in byte[] buffer form, for the lineOut.write() method.
        byte[] clip = new byte[ActualSamples_Int.size()];
        for(int i = 0; i < ActualSamples_Int.size(); i++) {
            clip[i] = ActualSamples_Int.get(i).byteValue();
        }

        AudioFormat linearPCM = new AudioFormat(8000,8,1,true,false);
        SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
        lineOut.open(linearPCM,32000);
        lineOut.start();
        lineOut.write(clip,0,256*N_OF_SOUND_PACKETS); //each sound Packet we receice corresponds to 256 bytes of sound.
        lineOut.stop();
        lineOut.close();
        //

        //closing the UDP sockets opened before
        s.close();
        r.close();

        sound_samples_diff.close();
        sound_samples.close();
//        sound_mean_value.close();
//        sound_steps.close();


        //Print message to indicate the end of echo packets interchange
        System.out.print("Sound Sample Download and Play END ");
        console_full.write("Sound Sample Download and Play END ".getBytes());
        System.out.print(formatter.format(date));
        console_full.write(formatter.format(date).getBytes());
        System.out.print(" \n \n \n");
        console_full.write(" \n \n \n".getBytes());
    }



private static void getSoundfromAQDPCM(String samples_diff_filename, String samples_filename,String meanValues_filename,
                                             String steps_filename,String Sound_request_params) throws IOException, LineUnavailableException {

    //masks to get the nibbles
    int nibble1mask = 0b11110000;
    int nibble2mask = 0b00001111;

    //values of B (beta) and mean value (mean) in DPCM
    int beta = 0 ;
    int mean_value= 0;


    //Print message to indicate the beginning of echo packets interchange
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    System.out.print("Sound Sample Download START ");
    System.out.println(formatter.format(date));
    System.out.print("\n");
    //In consolte.txt is written all the readable output
    console_full.write("Sound Sample START ".getBytes());
    console_full.write(formatter.format(date).getBytes());
    console_full.write("\n".getBytes());

    //opening streams to the files where data will be stored
    sound_samples_diff = new FileOutputStream( samples_diff_filename + ".csv");
    sound_samples = new FileOutputStream( samples_filename + ".csv");
    sound_mean_value = new FileOutputStream( meanValues_filename + ".csv");
    sound_steps = new FileOutputStream( steps_filename + ".csv");

    //Datagram Socket for sending request packets initialization.
    DatagramSocket s = new DatagramSocket();

    //Creating the request string from the function input arguments.
    String request_string = audio_request_code + Sound_request_params;
    byte[] txbuffer = request_string.getBytes();
    InetAddress hostAddress = InetAddress.getByAddress(hostIP);
    DatagramPacket clientRequest = new DatagramPacket(txbuffer,txbuffer.length,
            hostAddress,server_listening_port);

    DatagramSocket r = new DatagramSocket(client_listening_port);
    r.setSoTimeout(8000);
    //Set the size to 132, because for AQDPCM applications that's the length of the packets we reveice in bytes.
    byte[] rxbuffer = new byte[132];
    DatagramPacket serverResponse = new DatagramPacket(rxbuffer,rxbuffer.length);
    s.send(clientRequest);


    //ArrayList<Byte> ActualSamples_Bytes= new ArrayList<>(); // arraylists for store the actual 2 samples in bytes after demodulation
    ArrayList<Integer> ActualSamples_Int= new ArrayList<>(); // arraylists for store the actual 2 samples values after demodulation

    byte[] meanValueArr = new byte[4];
    byte[] quant_step = new byte[4];
    byte sign;

    // the remaining number of sound packets
    int nOfPacketsRemaining = N_OF_SOUND_PACKETS;
    do {
        try {

            r.receive(serverResponse);


            sign = (byte)( ( rxbuffer[1] & 0x80) !=0 ? 0xff : 0x00);
            meanValueArr[3] = sign;
            meanValueArr[2] = sign;
            meanValueArr[1] = rxbuffer[1];
            meanValueArr[0] = rxbuffer[0];
            int meanValue = ByteBuffer.wrap(meanValueArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
            sound_mean_value.write((  (meanValue) +"\n"   ).getBytes());
            sign = (byte)( ( rxbuffer[3] & 0x80) !=0 ? 0xff : 0x00);
            quant_step[3] = sign;
            quant_step[2] = sign;
            quant_step[1] = rxbuffer[3];
            quant_step[0] = rxbuffer[2];
            beta = ByteBuffer.wrap(quant_step).order(ByteOrder.LITTLE_ENDIAN).getInt();
            sound_steps.write((  (beta) +"\n"   ).getBytes());

            for (int i = 4; i < 132; i++) {
                int diff1, diff2, sample1;
                int sample2=0;

                // Getting the two difference between 2 samples from the two nibbles
                diff1 =( ((nibble1mask & rxbuffer[i]) >> 4) - 8 ) ;
                diff2 =( (nibble2mask & rxbuffer[i]) - 8) ;
                sound_samples_diff.write((  (diff1) +"\n"   ).getBytes());
                sound_samples_diff.write((  (diff2) +"\n"   ).getBytes());


                //getting the two samples values
                sample1 = diff1* beta + sample2 + meanValue;
                sample2 = diff2* beta + diff1* beta + meanValue;

                sound_samples.write((  ( sample1 & 0x000000FF)      +"\n"   ).getBytes());
                sound_samples.write((  ((sample1 & 0x0000FF00)>> 8) +"\n"   ).getBytes());
                sound_samples.write((  ( sample2 & 0x000000FF)      +"\n"   ).getBytes());
                sound_samples.write((  ((sample2 & 0x0000FF00)>> 8) +"\n"   ).getBytes());

                //storring values of 2 samples difference in order
                ActualSamples_Int.add(( sample1 & 0x000000FF) );
                ActualSamples_Int.add(((sample1 & 0x0000FF00)>> 8));
                ActualSamples_Int.add(( sample2 & 0x000000FF) );
                ActualSamples_Int.add(((sample2 & 0x0000FF00)>> 8)  );
            }




        } catch (Exception x) {
            System.out.println(x);
        }

        //Decrease the number of remaining packets.
        nOfPacketsRemaining--;
    }while (nOfPacketsRemaining > 0);


    //Getting our clip in byte[] buffer form, for the lineOut.write() method.
    byte[] clip = new byte[ActualSamples_Int.size()];
    for(int i = 0; i < ActualSamples_Int.size(); i++) {
        clip[i] = ActualSamples_Int.get(i).byteValue();
    }

    AudioFormat linearPCM = new AudioFormat(8000,16,1,true,false);
    SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
    lineOut.open(linearPCM,32000);
    lineOut.start();
    lineOut.write(clip,0,256*N_OF_SOUND_PACKETS); //each sound Packet we receice corresponds to 256 bytes of sound.
    lineOut.stop();
    lineOut.close();
    //

    //closing the UDP sockets opened before
    s.close();
    r.close();

    sound_samples_diff.close();
    sound_samples.close();
//        sound_mean_value.close();
//        sound_steps.close();


    //Print message to indicate the end of packets interchange
    System.out.print("Sound Sample Download and Play END ");
    console_full.write("Sound Sample Download and Play END ".getBytes());
    System.out.print(formatter.format(date));
    console_full.write(formatter.format(date).getBytes());
    System.out.print(" \n \n \n");
    console_full.write(" \n \n \n".getBytes());
}

private static void IthakiCopter(String telemetry_filename) throws IOException {
    //Print message to indicate the beginning ithaki_copter_telemetry
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    System.out.print("Ithaki copter Telemetry START ");
    System.out.println(formatter.format(date));
    System.out.print("\n");

    //In consolte.txt is written all the readable output
    console_full.write("Ithaki copter Telemetry START ".getBytes());
    console_full.write(formatter.format(date).getBytes());
    console_full.write("\n".getBytes());

    telemetry=new FileOutputStream( telemetry_filename + ".csv");

    DatagramSocket r = new DatagramSocket(48078);
    r.setSoTimeout(8000);
    byte[] rxbuffer = new byte[2048];
    DatagramPacket serverResponse = new DatagramPacket(rxbuffer,rxbuffer.length);

    String response, TelemetryOutput = "";
    String LLL, RRR, AAA, TTTT, PPPP;
    // We get 40 responses for telemetry
    for(int i=0; i<40 ;i++) {
        //s.send(clientRequest);
        try {
            r.receive(serverResponse);
            String telemPacket = new String(rxbuffer,0,serverResponse.getLength());
            System.out.println(telemPacket);
            console_full.write((telemPacket+"\n").getBytes());
            LLL = telemPacket.substring(40, 43);
            RRR = telemPacket.substring(51, 54);
            AAA = telemPacket.substring(64, 67);
            TTTT = telemPacket.substring(80, 86);
            PPPP = telemPacket.substring(96, 103);
            //System.out.println(LLL + " " + RRR + " " + AAA + " " + TTTT + " " + PPPP);
            telemetry.write((  (LLL + "," + RRR + "," + AAA + "," + TTTT + "," + PPPP + "\n")    ).getBytes());
        } catch (Exception x) {
            System.out.println(x);
        }
    }


    //Print message to indicate the end of packets interchange
    System.out.print("Ithaki copter Telemetry END ");
    console_full.write("Ithaki copter Telemetry ".getBytes());
    System.out.print(formatter.format(date));
    console_full.write(formatter.format(date).getBytes());
    System.out.print(" \n \n \n");
    console_full.write(" \n \n \n".getBytes());
    telemetry.close();
}



}

