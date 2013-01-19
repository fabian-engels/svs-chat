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
 *
 */
public class Client {
    
    private static Scanner in = new Scanner(System.in);
    private static int port = -1;
    
    public static void main( String[] args ) throws SocketException, UnknownHostException{
        
        int recivePort = 9602;
        String inputLine = "";
        byte[] data;
        
        if(port == -1){
            askForPort();
        }
        
        InetAddress ia = InetAddress.getByName("37.5.38.43");
        DatagramSocket dsocket = new DatagramSocket(port); //UDP
        DatagramSocket reciveDsocket = new DatagramSocket(recivePort);
                  
        try {
            while(!inputLine.equalsIgnoreCase("/close")){
                inputLine = in.nextLine();
                data = inputLine.getBytes();

//                System.out.println("Sendet data length: " + data.length);

                DatagramPacket dPackage = new DatagramPacket(data, data.length, ia, port);

                dPackage.setPort(9600);
                dsocket.send(dPackage);

                reciveDsocket.receive(dPackage);

                byte[] buffer = dPackage.getData();
                    String text = new String(buffer,"UTF8");
                    System.out.println("Received Data: " + text);           

            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void askForPort(){
        
        System.out.println("Please choose a server port! (/port ...)");
        
        String[] inPut = in.nextLine().split(" ");
        
        if(inPut[0].contains("/port") && inPut.length != 1 && inPut[1].matches("\\d+")){
            System.out.println("/port " + inPut[1] + " set.");
            port = Integer.parseInt(inPut[1]);
        }else{
            askForPort();
        }
    }
}