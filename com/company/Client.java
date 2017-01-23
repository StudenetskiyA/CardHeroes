package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by samsung on 06.01.2017.
 */
public class Client {
    private static BufferedReader in;
    private static PrintWriter out;
    private static Socket socket;

    static void disconnect() throws IOException {
        socket.close();
    }
    static void connect(int serverPort, String address){
        try {
            System.out.println("Try to connect to socket with IP address " + address + " and port " + serverPort + ".");
            // Setup networking
            socket = new Socket(address, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"windows-1251"));
          //  out = new PrintWriter(socket.getOutputStream(), true);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "windows-1251"), true);
            System.out.println("Yes! Connection ok.");
            Main.connected=true;
        }
        catch (Exception x) {
           // x.printStackTrace();
        }
    }

    static String readLine() throws IOException {
        if (Main.connected)
        return in.readLine();
        return null;
    }
    static void writeLine(String line){
        if (Main.connected)
        out.println(line);
    }
}
