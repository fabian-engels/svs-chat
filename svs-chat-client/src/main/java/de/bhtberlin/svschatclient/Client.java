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
        
        int port = 9600;
        int recivePort = 9602;
        InetAddress ia = InetAddress.getByName("localhost");
        
        
        DatagramSocket ds = new DatagramSocket(port); //UDP
        DatagramSocket reciveDs = new DatagramSocket(recivePort);
        
        String s = "Wer andere links liegen lässt, steht rechts.";
        byte[] data = s.getBytes();
        
        System.out.println("Sendet data length: " + data.length);
        
        DatagramPacket dm = new DatagramPacket(data, data.length, ia, port);
               
        try {
            ds.send(dm);
            ds.close();
            
            reciveDs.receive(dm);
                       
            byte[] buffer = dm.getData();
                String text = new String(buffer);
                System.out.println("Received Data: " + text);
            
            reciveDs.close();                
                
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}