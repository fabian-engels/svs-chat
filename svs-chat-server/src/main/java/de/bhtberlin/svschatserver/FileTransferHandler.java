/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nto
 */
public class FileTransferHandler implements Runnable {
    
    private DatagramSocket serverSocket;
    private DatagramPacket packet;
    private InetAddress destination;
    private final int bufferSize;
    
    public FileTransferHandler(int buffersize, InetAddress destination, DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.bufferSize = buffersize;
        this.destination = destination;
    }
    
    public void run() {
        while(true) {
              try {
                this.packet = new DatagramPacket(new byte[this.bufferSize], this.bufferSize); 
                this.serverSocket.receive(this.packet);
                this.packet.setAddress(destination);
                this.serverSocket.send(packet);
                if(this.packet.getLength()!=this.bufferSize){
                    this.serverSocket.close();
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
