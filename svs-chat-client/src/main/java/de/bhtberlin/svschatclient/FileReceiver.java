package de.bhtberlin.svschatclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nto
 */
class FileReceiver implements Runnable {

    private DatagramSocket receiveSocket;
    private final int bufferSize;
    private DatagramPacket dp;

    public FileReceiver(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {

        try {
            this.receiveSocket = new DatagramSocket(9604);
        } catch (SocketException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] buf = new byte[this.bufferSize];

        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                try {
                    this.dp = new DatagramPacket(buf, buf.length);
                    receiveSocket.receive(this.dp);
                    handleFilePackage(dp.getData());
                } catch (IOException ex) {
                    Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Received FileData: " + new String(this.dp.getData()));
            }
        }
    }
    File file;
    int offset = 0;
    FileOutputStream fo;

    private void handleFilePackage(final byte[] input) {
        StringTokenizer st = new StringTokenizer(new String(input));
        String token = st.nextToken();
        byte[] cleanInput = new String(input).trim().substring(token.length()+1).getBytes();
        if (token.matches("/file")) {
            String name = st.nextToken();
            file = new File("J:\\data.txt");
            offset = 0;
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                fo = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                fo.write(cleanInput);
            } catch (IOException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (token.matches("/eofe")) {
            try {
                fo.close();
            } catch (IOException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (token.matches("/part")) {
            try {
                fo.write(cleanInput);
            } catch (IOException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
