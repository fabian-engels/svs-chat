/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MessageSender pools the given queue for new messages and sends them out by
 * DatagramSocket.
 *
 * @author nto
 */
class FileSender implements Runnable {

    private final DatagramSocket sendSocket;
    private final int targetServerPort;
    private final InetAddress serverAddress;
    private final File file;
    private final String receiverName; //TODO implementieren

    public FileSender(String receiverName, File file, final int targetServerPort, final InetAddress serverAddress, final DatagramSocket socket) {
        this.file = file;
        this.sendSocket = socket;
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
                dp.setPort(targetServerPort);
                dp.setAddress(serverAddress);
                sendSocket.send(dp);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioex){
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ioex);
        }
    }
}