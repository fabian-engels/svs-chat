package de.bhtberlin.svschatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class Server 
{
    public static void main( String[] args )
    {
        DatagramSocket sendSocket;
        DatagramSocket serverSocket;
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        try{
            sendSocket = new DatagramSocket(9602);
            serverSocket = new DatagramSocket(9600);
            while(true){
                serverSocket.receive(packet);
                System.out.println("Received from IP:" + packet.getAddress());
                System.out.println("Received Data: " + packet.getData().toString());
                InetAddress address = packet.getAddress();
                packet.setAddress(address);
                sendSocket.send(packet);
                System.out.println("Packet got send!");
            }
        }catch(SocketException ex){
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
