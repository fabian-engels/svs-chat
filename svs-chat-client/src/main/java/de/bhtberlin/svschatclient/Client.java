package de.bhtberlin.svschatclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

/**
 * SVS UDP Client Chat v0.01
 * Sven Höche, Fabian Engels
 *
 */
public class Client {
    
    public static void main( String[] args ) throws SocketException, UnknownHostException{
        
        int port = 9600;
        int recivePort = 9602;
        Scanner in = new Scanner(System.in);
        String inputLine = "";
        byte[] data;
        
        InetAddress ia = InetAddress.getByName("37.5.38.43");
        DatagramSocket dsocket = new DatagramSocket(port); //UDP
        DatagramSocket reciveDsocket = new DatagramSocket(recivePort);
                  
        try {
            while(inputLine.equalsIgnoreCase("close")){
                inputLine = in.nextLine();
                data = inputLine.getBytes();

                System.out.println("Sendet data length: " + data.length);

                DatagramPacket dPackage = new DatagramPacket(data, data.length, ia, port);

                dPackage.setPort(9600);
                dsocket.send(dPackage);
                dsocket.close();

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