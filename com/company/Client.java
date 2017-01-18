package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
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
            System.out.println("Try to connect to socket with IP address " + address + " and port " + serverPort + "?");
            // Setup networking
            socket = new Socket(address, serverPort);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Yes! Connection ok.");
        }
        catch (Exception x) {
            x.printStackTrace();
        }
    }

    static String readLine() throws IOException {
        return in.readLine();
    }
    static void writeLine(String line){
        out.println(line);
    }
}
