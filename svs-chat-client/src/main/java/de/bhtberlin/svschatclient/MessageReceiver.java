package de.bhtberlin.svschatclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nto
 */
class MessageReceiver implements Runnable {

    private DatagramSocket receiveSocket;
    private final int bufferSize;
    private DatagramPacket dp;

    public MessageReceiver(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
        
        try {
            this.receiveSocket = new DatagramSocket(9602);
        } catch (SocketException ex) {
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] buf = new byte[this.bufferSize];

        while (!Thread.currentThread().isInterrupted()) {
            synchronized(this){
                
            try {
                this.dp = new DatagramPacket(buf, buf.length);
                receiveSocket.receive(this.dp);
            } catch (IOException ex) {
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
                System.out.println("Received: " + new String(this.dp.getData()));
            }
        }
    }
    
}
