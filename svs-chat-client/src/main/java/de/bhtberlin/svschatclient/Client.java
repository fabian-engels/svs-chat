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
 * SVS UDP Client Chat v0.01
 * Sven Höche, Fabian Engels
 * timeout, server
 */
public class Client {
    
    private Scanner in;
    private int port = -1;
    
    private final int recivePort = 9602;
    private String inputLine = "";
    private byte[] data;
    
    private DatagramSocket dsocket;
    private DatagramSocket reciveDsocket;
    private InetAddress ia;
    private DatagramPacket dPackage;
          
    public Client() throws UnknownHostException{
        this.ia = InetAddress.getByName("37.5.38.43");
        this.in = new Scanner(System.in);
    }
    
    public void run() throws SocketException {
        if(port == -1){
            askForPort();
        }
        
        this.dsocket = new DatagramSocket(port); //UDP
        this.reciveDsocket = new DatagramSocket(recivePort);
        
        Thread receiverThread = new Thread(new ReceiverThread(recivePort));
        receiverThread.start();
        
        try {
            while(!inputLine.equalsIgnoreCase("/close")){
                this.inputLine = in.nextLine();
                this.data = inputLine.getBytes();
                
                this.dPackage = new DatagramPacket(data, data.length, ia, port);

                this.dPackage.setPort(9600);
                this.dsocket.send(dPackage);
            }
            receiverThread.interrupt();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main( String[] args ) throws SocketException, UnknownHostException{
        Client client = new Client();
        client.run();
    }
    
    public void askForPort(){
        
        System.out.println("Please choose a server port! (/port ...)");
        
        String[] inPut = this.in.nextLine().split(" ");
        
        if(inPut[0].contains("/port") && inPut.length != 1 && inPut[1].matches("\\d+")){
            System.out.println("/port " + inPut[1] + " set.");
            this.port = Integer.parseInt(inPut[1]);
        }else{
            askForPort();
        }
    }
    
    public class ReceiverThread extends Thread{

        private DatagramSocket reciveDsocket;
        
        public ReceiverThread(final int receivePort) throws SocketException{
            this.reciveDsocket = new DatagramSocket(receivePort);
        }
        
        @Override
        public void interrupt(){
            super.interrupt();
            reciveDsocket.close();
        }
        
        @Override
        public void run() {
            try {
                while(true){
                    reciveDsocket.receive(dPackage);

                    byte[] buffer = dPackage.getData();
                        String text = new String(buffer,"UTF8");
                        System.out.println("Received Data: " + text);
                }
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
}