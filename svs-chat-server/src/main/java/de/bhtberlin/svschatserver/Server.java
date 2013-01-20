package de.bhtberlin.svschatserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * SVS UDP Chat Server
 *
 * @author Fabian Engels, Sven Höche
 *
 */
public class Server {

    private DatagramSocket sendSocket;
    private DatagramSocket serverSocket;
    private DatagramPacket packet;
    private Set<InetAddress> clients;
    final Integer receivePort = 9600;
    final Integer sendFromPort = 9601;
    final Integer sendToPort = 9602;

    public Server() {
        this.clients = new HashSet();
    }

    public void run() {
        try {
            this.serverSocket = new DatagramSocket(this.receivePort);
            this.sendSocket = new DatagramSocket(this.sendFromPort);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            this.serverSocket.close();
            this.sendSocket.close();
        }
        while (true) {
            try {
                this.packet = new DatagramPacket(new byte[1024], 1024); 
                this.serverSocket.receive(this.packet);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

            InetAddress srcAddress = packet.getAddress();
            System.out.println("Received from IP:" + srcAddress+":"+packet.getPort());
            this.clients.add(srcAddress);

            execCommands(packet);
            responde(packet);
        }
    }

    public static void main(String[] args) {
        new Server().run();
    }

    private void responde(DatagramPacket packet) {
        packet.setPort(sendToPort);
        for (InetAddress iaddr : clients) {
            packet.setAddress(iaddr);
            try {
                this.sendSocket.send(packet);
                System.out.println("Packet send to: "+packet.getAddress()+":"+packet.getPort());
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void execCommands(DatagramPacket packet) {
        byte[] data = packet.getData();
        String text = null;
        try {
            text = new String(data, "UTF8");
            System.out.println("Received Data: " + text);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        Pattern closePat = Pattern.compile("/close.+");
        Pattern unknownPat = Pattern.compile("unknown");

        if (closePat.matcher(text).matches()) {
            clients.remove(packet.getAddress());
        }
        if (unknownPat.matcher(text).matches()) {
            //@TODO notify new client
        }
    }
}
