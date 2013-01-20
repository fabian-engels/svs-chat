package de.bhtberlin.svschatclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SVS UDP Client Chat v0.01 Sven Höche, Fabian Engels timeout, server
 */
public class Client {

    private Scanner in;
//    private int port = -1;
    private int port = 0;
    private final int recivePort = 9602;
    private final int targetPort = 9600;
    private String inputLine = "";
//    private byte[] data;
    private DatagramSocket dsocket;
    private InetAddress ia;
    private DatagramPacket dPackage;

    public Client() {
        try {
            this.ia = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.in = new Scanner(System.in);
    }

    public void run() {
        displayUsage();
        if (port == -1) {
            askForPort();
        }
        try {
            this.dsocket = new DatagramSocket(port); //UDP
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        Thread receiverThread = new ReceiverThread(recivePort);
        receiverThread.start();

        try {
            while (!inputLine.equalsIgnoreCase("/close")) {
                this.inputLine = in.nextLine();
                StringBuilder sb = new StringBuilder();
                sb.append(this.ia.getHostAddress());
                sb.append(": ");
                sb.append(this.inputLine);
                byte[] data = sb.toString().getBytes();

                this.dPackage = new DatagramPacket(data, data.length, ia, port);

                this.dPackage.setPort(targetPort);
                this.dsocket.send(dPackage);
            }
            receiverThread.interrupt();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    public void displayUsage() {
        final String text =
                "SVS UDP Chat Client\n"
                + "*usage* to the change the targeted port type: /port <number>";
        System.out.println(text);
    }

    public void askForPort() {
        System.out.println("Please choose a server port! (/port ...)");

        String[] inPut = this.in.nextLine().split(" ");
        final String portRegEx = "/port";

        if (inPut[0].contains(portRegEx) && inPut.length != 1 && inPut[1].matches("\\d+")) {
            System.out.println(portRegEx + inPut[1] + " set.");
            this.port = Integer.parseInt(inPut[1]);
        } else {
            askForPort();
        }
    }

    public class ReceiverThread extends Thread {

        private DatagramSocket datagramSocket;
        private int port;
        private int bufferSize = 256;

        public ReceiverThread(int port) {
            this.port = port;
            System.out.println("Listen on port: " + port);
        }

        @Override
        public void interrupt() {
            super.interrupt();
            if (datagramSocket != null) {
                this.datagramSocket.close();
            }
        }

        @Override
        public void run() {
            try {
                this.datagramSocket = new DatagramSocket(port);
            } catch (SocketException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            byte[] buf;
            DatagramPacket dp;
            try {
                while (true) {
                    buf = new byte[bufferSize];
                    dp = new DatagramPacket(buf, buf.length);
                    this.datagramSocket.receive(dp);
                    byte[] textBuf = dp.getData();

                    /* Begin  debug output */
                    for (byte b : textBuf) {
                        System.out.print(b);
                    }
                    System.out.println();
                    /* End of debug output */
                    String text = new String(textBuf, "UTF8");
                    System.out.println("Received Data: " + text);
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}