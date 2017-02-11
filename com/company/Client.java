package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by samsung on 06.01.2017.
 */
public class Client {
    private static BufferedReader in;
    private static PrintWriter out;
    private static Socket socket;
    static int trying=0;
    static void disconnect() throws IOException {
        socket.close();
    }
    static void connect(int serverPort, String address){
        try {
            System.out.println("Try("+trying+") to connect to socket with IP address " + address + " and port " + serverPort + ".");
            // Setup networking
            socket = new Socket(address, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"windows-1251"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "windows-1251"), true);
            System.out.println("Yes! Connection ok.");
            Main.connected=true;
        }
        catch (Exception x) {
            //Another try
            try {
                if (trying>=5){return;}
                TimeUnit.SECONDS.sleep(2);
                trying++;
                connect(serverPort,address);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //x.printStackTrace();
        }
    }

    static String readLine(){
        if (Main.connected)
            try {
                return in.readLine();
            } catch (IOException e) {
            //TODO Message of server down
                //e.printStackTrace();
                System.out.println("Server down.");
                System.exit(2);
            }
        return null;
    }
    static void writeLine(String line){
        Main.ready=false;
        if (Main.connected) {
            out.println(line);
           // System.out.println("I send "+line);
        }
    }
}
