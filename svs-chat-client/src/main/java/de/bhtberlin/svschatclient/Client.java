package de.bhtberlin.svschatclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SVS UDP Client Chat v0.01
 * Sven Höche, Fabian Engels
 *
 */
public class Client {
    
    public static void main( String[] args ) throws SocketException, UnknownHostException{
        
        int port = 7070;
        int recivePort = 7072;
        InetAddress ia = InetAddress.getByName("localhost");
        
        DatagramSocket ds = new DatagramSocket(port); //UDP
        DatagramSocket reciveDs = new DatagramSocket(recivePort);
        
        String s = "Wer andere links liegen lässt, steht rechts.";
        byte[] data = s.getBytes();
        
        System.out.println("Sendet data length: " + data.length);
        
        DatagramPacket dPackage = new DatagramPacket(data, data.length, ia, port);
               
        try {
            dPackage.setPort(9600);
            ds.send(dPackage);
            ds.close();
            
            dPackage.setPort(9602);
            reciveDs.receive(dPackage);
                       
            byte[] buffer = dPackage.getData();
                String text = new String(buffer,"UTF8");
                System.out.println("Received Data: " + text);           
                
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}