package de.bhtberlin.svschatserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * SVS UDP Chat Server
 *
 * @version v0.01
 * @author Fabian Engels
 * @author Sven Höche
 *
 */
public class Server {

    private DatagramSocket sendSocket;
    private DatagramSocket serverSocket;
    private DatagramPacket packet;
    private Map<InetAddress, Set<String>> clients;
    final Integer receivePort = 9600;
    final Integer sendFromPort = 9601;
    final Integer sendToPort = 9602;

    public Server() {
        this.clients = new HashMap<InetAddress, Set<String>>();
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
            System.out.println("Received from IP:" + srcAddress + ":" + packet.getPort());
            String name = splitName(packet);

            Set<String> tmpnames = clients.get(srcAddress);
            if (tmpnames == null) {
                tmpnames = new HashSet<String>();
            }
            tmpnames.add(name);
            clients.put(srcAddress, tmpnames);

            execCommands(packet);
            responde(packet);
        }
    }

    private String[] splitText(DatagramPacket packet, String regex) {
        String text = new String(packet.getData());
        return text.split(regex);
    }

    private String splitName(DatagramPacket packet) {
        String[] text = splitText(packet, ":");
        if (text == null || text.length < 2) {
            return "EmptyName";
        }
        return text[0];
    }

    public static void main(String[] args) {
        new Server().run();
    }

    private void responde(DatagramPacket packet) {
        String name = splitName(packet);
        if (name == null) {
            return;
        }
        packet.setPort(sendToPort);
        for (InetAddress iaddr : clients.keySet()) {
            if (clients.get(iaddr).contains(name)) {
                continue;
            } else {
                packet.setAddress(iaddr);
                String[]arr = splitText(packet, "/name");
                if(arr.length >= 2) {
                    packet.setData(splitText(packet, "/name")[1].getBytes());
                }
            }
            try {
                this.sendSocket.send(packet);
                System.out.println("Packet send   : " + new String(packet.getData()));
                System.out.println("Packet send to: " + packet.getAddress() + ":" + packet.getPort());
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
        Pattern filePat = Pattern.compile("/file.+");
        Pattern unknownPat = Pattern.compile("unknown");

        if (closePat.matcher(text).matches()) {
            clients.remove(packet.getAddress());
        }
        if (unknownPat.matcher(text).matches()) {
            //@TODO notify new client
        }
        if (filePat.matcher(text).matches()) {
            String[] split = text.split("/file");
            if (split == null) {
                // no file as argument
            } else {
                //new FileTransferHandler()
            }
        }
    }
}
