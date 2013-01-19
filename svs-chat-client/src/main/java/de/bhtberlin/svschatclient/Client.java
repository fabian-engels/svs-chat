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
        
        InetAddress ia = InetAddress.getByName("37.5.38.43");
        DatagramPacket dm;
        DatagramSocket ds = new DatagramSocket(port); //UDP
        DatagramSocket reciveDs = new DatagramSocket(recivePort);
        
        String s = "Wer andere links liegen lässt, steht rechts.";
        byte[] data = s.getBytes();
        dm = new DatagramPacket(data, data.length, ia, port);
        
        try {
            ds.send(dm);
            
            reciveDs.receive(dm);
            System.out.println(dm.getData().toString());
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}