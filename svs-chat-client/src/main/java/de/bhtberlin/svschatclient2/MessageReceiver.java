package de.bhtberlin.svschatclient2;

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

        while (true) {
            try {
                this.dp = new DatagramPacket(buf, buf.length);
                receiveSocket.receive(this.dp);
            } catch (IOException ex) {
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.INFO, new String(this.dp.getData()));
            System.out.println("Received: " + new String(this.dp.getData()));
        }
    }
    DatagramPacket dp;
}
