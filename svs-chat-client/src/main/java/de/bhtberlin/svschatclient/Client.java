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
 * SVS UDP Chat Client
 *
 * @version v0.01
 * @author Sven Höche
 * @author Fabian Engels
 *
 * Notes: - Timeout value for incoming messages has to be only limited for own
 * messages.
 * Name, Name
 * 
 */
public class Client {

    private final int localPort = 0;
    private final int recivePort = 9602;
    private int targetPort = 9600;
    private String clientName = "";
    private String serverIP = "37.5.33.49";
    
    private Scanner in;
    private String inputLine = "";
    private DatagramSocket dsocket;
    private InetAddress ia;
    private DatagramPacket dPackage;
    private Thread receiverThread;
    private boolean wasProcessLine = false;
    
    private final String portRegEx = "/port";
    private final String nameRegEx = "/name";
    private final String serverIPRegEx = "/ip";

    public Client() {
        this.in = new Scanner(System.in);
        if (!serverIP.isEmpty()){
            try {
                this.ia = InetAddress.getByName(serverIP);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            askForServerIP();
        }
    }

    public void run() {
        displayUsage();

        if (targetPort == -1) {
            askForPort();
        }
        if(clientName.isEmpty()){
            askForClientName();
        }
        try {
            this.dsocket = new DatagramSocket(localPort); //UDP
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        receiverThread = new ReceiverThread(recivePort);
        receiverThread.start();

        try {
            while (true) {
                System.out.print(clientName + ": ");
                System.out.flush();
                this.inputLine = in.nextLine();
                processInput(this.inputLine);
                
                this.inputLine = nameRegEx + " " + clientName + ":" + this.inputLine;
                
                if(wasProcessLine == true){
                    wasProcessLine = false;
                    continue;
                }
                
                byte[] data = this.inputLine.getBytes();

                this.dPackage = new DatagramPacket(data, data.length, ia, localPort);

                this.dPackage.setPort(targetPort);
                this.dsocket.send(dPackage);
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processInput(final String input) {
        String[] args = input.split(" "); // Was: \\W

        if (args.length > 0 && args[0].equalsIgnoreCase("/close")) {
           this.receiverThread.interrupt();
           this.dsocket.close();
           wasProcessLine = true;
           System.out.println("Program is shuting down.");
           System.exit(0);
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("/port")) {
           this.targetPort = Integer.parseInt(args[1]);
           System.out.println("New target /port " + this.targetPort + " set.");
           wasProcessLine = true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("/ip")) {
           this.serverIP = args[1];
           System.out.println("New server /ip " + this.serverIP + " set.");
           wasProcessLine = true;
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

        if (inPut[0].contains(portRegEx) && inPut.length > 1 && inPut[1].matches("\\d+")) {
            System.out.println(portRegEx + " " + inPut[1] + " set.");
            this.targetPort = Integer.parseInt(inPut[1]);
        } else {
            askForPort();
        }
    }

    private void askForClientName() {
        System.out.println("Please choose a Name! (/name ...)");
        
        String[] inPut = this.in.nextLine().split(" ");
        
        if (inPut[0].contains(nameRegEx) && inPut.length > 1 && inPut[1].matches("\\w+")) {
            System.out.println(nameRegEx + " " + inPut[1] + " set.");
            for(int i = 1; i<inPut.length; i++){
                this.clientName = clientName + inPut[i] + " ";
            }
        }else{
            askForClientName();
        }
    }

    private void askForServerIP() {
        System.out.println("Please choose a Server-IP! (/ip ...)");
        
        String[] inPut = this.in.nextLine().split(" ");
        
        if (inPut[0].contains(serverIPRegEx) && inPut.length > 1 && inPut[1].matches("\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b")) {
            System.out.println(serverIPRegEx + " " + inPut[1] + " set.");
            try {
                this.serverIP = inPut[1];
                this.ia = InetAddress.getByName(this.serverIP);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            askForServerIP();
        }
    }

    public class ReceiverThread extends Thread {

        private DatagramSocket datagramSocket;
        private int port;
        private final int bufferSize = 256;

        public ReceiverThread(final int port) {
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
                    /* 
                     * for (byte b : textBuf) { System.out.print(b); }
                     * System.out.println();
                     */
                    String text = new String(textBuf, "UTF8");
                    StringBuilder sb = new StringBuilder();
                    sb.append(dp.getAddress().toString().substring(1));
                    sb.append("> ");
                    sb.append(text);
                    System.out.println(sb);
                    System.out.print(clientName + ": ");
                    System.out.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}