package de.bhtberlin.svschatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

/**
 * SVS UDP Client Chat v0.01
 * Sven Höche, Fabian Engels
 *
 */
public class Server 
{
    public static void main( String[] args )
    {
        DatagramSocket sendSocket;
        DatagramSocket serverSocket;
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        Set <InetAddress> clients = new HashSet();
        try{
            serverSocket = new DatagramSocket(9600);
            while(true){
                serverSocket.receive(packet);
                System.out.println("Received from IP:" + packet.getAddress());
                byte[] buffer = packet.getData();
                String text = new String(buffer, "UTF8");
                
                System.out.println("Received Data: " + text);
                InetAddress sourceAddr = packet.getAddress();
                clients.add(sourceAddr);
                if(text.equalsIgnoreCase("close")){
                  clients.remove(sourceAddr);  
                }
                packet.setPort(9602);
                sendSocket = new DatagramSocket(9601);
                for(InetAddress iaddr: clients){
                    packet.setAddress(iaddr);
                    sendSocket.send(packet);
                }
                sendSocket.close();
                System.out.println("Packet got send!");
            }
        }catch(SocketException ex){
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
