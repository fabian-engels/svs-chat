package de.bhtberlin.svschatclient2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author nto
 */
 class MessageReceiver implements Runnable {
        private final DatagramSocket receiveSocket;
        private final int bufferSize;

        public MessageReceiver(int bufferSize, DatagramSocket receiveSocket) {
            this.receiveSocket = receiveSocket;
            this.bufferSize = bufferSize;
        }
        
        @Override
        public void run(){
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.INFO, new String("running..."));
            
            byte[] buf = new byte[this.bufferSize];
            DatagramPacket dp = new DatagramPacket(buf, bufferSize);
            /**
             * @TODO fix IllegalStateException
             */
            while(!Thread.currentThread().isInterrupted()) {
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.INFO, new String("receiving..."));
                    try {
                        receiveSocket.receive(dp);
                    } catch (IOException ex) {
                        Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Logger.getLogger(MessageReceiver.class.getName()).log(Level.INFO, new String(dp.getData()));
                    System.out.println(new String(dp.getData()));
            }
        }
    }
