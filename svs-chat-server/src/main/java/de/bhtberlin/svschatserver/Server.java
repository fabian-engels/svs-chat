package de.bhtberlin.svschatserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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
    final int bufferSize = 1024;

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
                this.packet = new DatagramPacket(new byte[bufferSize], bufferSize);
                this.serverSocket.receive(this.packet);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

            InetAddress srcAddress = packet.getAddress();
            System.out.println("Received from IP:" + srcAddress + ":" + packet.getPort());
            String text = new String(packet.getData());
            StringTokenizer st = new StringTokenizer(text, ":");
            String name = st.nextToken();
            if (name == null) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Error while parsing " + text);
            }

            Set<String> tmpnames = clients.get(srcAddress);
            if (tmpnames == null) {
                tmpnames = new HashSet<String>();
            }
            tmpnames.add(name);
            clients.put(srcAddress, tmpnames);

            execCommands(packet);
            sendResponse(packet);
        }
    }

    private String[] splitText(DatagramPacket packet, String regex) {
        String text = new String(packet.getData());
        return text.split(regex);
    }

    private String splitNameAndText(DatagramPacket packet) {
        String[] text = splitText(packet, ":");
        if (text == null || text.length < 2) {
            return "EmptyName";
        }
        return text[0].replaceAll("/name", "");
    }

    public static void main(String[] args) {
        new Server().run();
    }

    private void sendResponse(final DatagramPacket packet) {
        Logger.getLogger(Server.class.getName()).log(Level.INFO, "responde() :: " + new String(packet.getData()));

        /*String name = splitNameAndText(packet);
         if (name == null) {
         return;
         }*/

        StringTokenizer st = new StringTokenizer(new String(packet.getData()));
        String name = st.nextToken(":");
        String text = st.nextToken(":");
        packet.setPort(sendToPort);

        /*  */
        for (InetAddress iaddr : clients.keySet()) {
            Set<String> tmpSet = clients.get(iaddr);
            for (String entry : tmpSet) {
                if (entry.equals(name)) {
                    packet.setAddress(iaddr);
                    try {
                        this.sendSocket.send(packet);
                        System.out.println("Packet send   : " + new String(packet.getData()));
                        System.out.println("Packet send to: " + packet.getAddress() + ":" + packet.getPort());
                        System.out.println("Packet send at: " + GregorianCalendar.getInstance().getTime().toString());
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
    }

    private InetAddress getInetAddrByName(String name) {
        Set<String> tmpSet;
        for (InetAddress iaddr : this.clients.keySet()) {
            tmpSet = this.clients.get(iaddr);
            if (tmpSet.contains(name)) {
                return iaddr;
            }
        }
        return null;
    }

    private void execCommands(final DatagramPacket packet) {
        byte[] data = packet.getData();
        String text = null;
        try {
            text = new String(data, "UTF8");
            System.out.println("Received Data: " + text);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        Pattern closePat = Pattern.compile("/close.+");
        Pattern filePat = Pattern.compile(".+/file.+");
        Pattern unknownPat = Pattern.compile("unknown");

        if (closePat.matcher(text).matches()) {
            clients.remove(packet.getAddress());
        }
        if (unknownPat.matcher(text).matches()) {
            //@TODO notify new client
        }
        /*  if (filePat.matcher(text).matches()) {

         String[] split = text.split("/file ");
         if (split == null) {
         // no file as argument
         } else {
         InetAddress destinationAddress = null;
         String[] split1 = split[1].split(":");
         destinationAddress = getInetAddrByName(split1[0]);

               
         DatagramSocket serverSocket = null;
         try {
         serverSocket = new DatagramSocket();
         } catch (SocketException ex) {
         Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
         }
         int fileTransferPort = serverSocket.getLocalPort();
                
         if(destinationAddress==null){
         return;
         }
                
         Thread thread = new Thread(new FileTransferHandler(bufferSize, destinationAddress, serverSocket));
         thread.start();
         String fileMsg = "filetrans@" + fileTransferPort;
         try {
         byte[] da00ta = fileMsg.getBytes();
         DatagramPacket dp = new DatagramPacket(da00ta, da00ta.length, packet.getAddress(), this.sendToPort);
         this.sendSocket.send(dp);
         } catch (Exception ex) {
         Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
         }

         }
         }*/
    }
}
