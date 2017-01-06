package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by samsung on 06.01.2017.
 */
public class Client {
    static DataInputStream in;
    static DataOutputStream out;
    public static boolean connected=false;

    static void connect(int serverPort, String address) throws IOException {
            InetAddress ipAddress = InetAddress.getByName(address); // создаем объект который отображает вышеописанный IP-адрес.
            System.out.println("Try to connect to socket with IP address " + address + " and port " + serverPort + "?");
            Socket socket;
            socket = new Socket(ipAddress, serverPort); // создаем сокет используя IP-адрес и порт сервера.
            System.out.println("Yes! Connection ok.");

            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиентом.
            OutputStream sout = socket.getOutputStream();
            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиентом.
            InputStream sin = socket.getInputStream();

            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            in = new DataInputStream(sin);

            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            out = new DataOutputStream(sout);

            connected=true;
    }

    static String readFromServer(){
        try {
            String line;
            while (true) {
                line = in.readUTF(); // ждем пока сервер отошлет строку текста.
                System.out.println("Server send "+line);
                if (line!=null)
                    return line;
            }
        } catch (Exception x) {
           // x.printStackTrace();
            return "$DISCONNECT";
        }
    }

    static void sendToServer(String text){
        if (connected){
        try {
            System.out.println("2Server: "+text);
            out.writeUTF(text); // отсылаем введенную строку текста серверу.
            out.flush(); // заставляем поток закончить передачу данных.
        }
        catch (Exception x) {
            x.printStackTrace();
        }
    }
    }

}
