/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MessageSender pools the given queue for new messages and sends them out by
 * DatagramSocket.
 *
 * @author nto
 */
class FileSender implements Runnable {

    private  DatagramSocket sendSocket;
    private  int targetServerPort;
    private  InetAddress serverAddress;
    private  File file;
    private  String receiverName; //TODO implementieren

    /**
     * 
     * @param receiverName
     * @param file
     * @param targetServerPort
     * @param serverAddress 
     */
    public FileSender(final String receiverName, final File file, final int targetServerPort, final InetAddress serverAddress) {
        this.file = file;
        try {
            this.sendSocket = new DatagramSocket(0);
        } catch (SocketException ex) {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.targetServerPort = targetServerPort;
        this.serverAddress = serverAddress;
        this.receiverName = receiverName;
    }
    
    @Override
    public void run() {
        try {
            DatagramPacket dp = null;

            FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(file);
            byte[] data = new byte[1024];

            while(fileInputStream.read(data) != -1){
                dp = new DatagramPacket(data, 1024);
                dp.setPort(this.targetServerPort);
                dp.setAddress(this.serverAddress);
                sendSocket.send(dp); // send?
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioex){
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ioex);
        }
    }
}