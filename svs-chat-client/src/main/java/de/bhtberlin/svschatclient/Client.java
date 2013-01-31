package de.bhtberlin.svschatclient;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nto
 */
public class Client {
    /** Buffersize which is used to receive DataGramPackets. */
    public static final int BUFFERSIZE = 1024;
    /** Queuesize which is used as limit for outgoing messages. */
    public static final int QUEUESIZE = 1000;
    /** DestinationPort for outgoing messages. */
    public static final int MESSAGEDESTINATIONPORT = 9600;

    /**
     * Main method to start all threads.
     * @param args String[]
     */
    public static void main(final String[] args) {
        Client main = new Client();
        main.init();
    }
    /**
     * Queue to share outgoing messages between
     * ConsoleReaderThread and MessageSenderThread.
     */
  private  BlockingQueue<String> bq = new ArrayBlockingQueue<String>(QUEUESIZE);
  /**
   * The InetAddress of the chat server.
   */
  private InetAddress iaddr;

  /**
   * Method to start of all threads included in main.
   */
    private void init() {
        iaddr = lookupIP("127.0.0.1");
        this.initMessageReceiver();
        this.iniFileReceiver();
        this.initMessageSender();
        this.initConsoleReader();
    }

    /**
     * Create ConsolerReader with BlockingQueue and InetAddress.
     * Start the ConsoleReaderThread.
     */
    private void initConsoleReader() {
        ConsoleReader cr = new ConsoleReader(bq, iaddr);
        Thread t3;
        t3 = new Thread(cr, "ConsoleReaderThread");
        t3.start();
    }

    /**
     * Create MessageReceiver with BUFFERSIZE.
     * Start the MessageReceiverThread.
     */
    private void initMessageReceiver() {
        MessageReceiver msr = new MessageReceiver(BUFFERSIZE);
        Thread t1 = new Thread(msr, "MessageReceiverThead");
        t1.start();
    }

    /**
     * Lookup a InetAddress by string represenation.
     * @param value String
     * @return InetAddress
     */
    private InetAddress lookupIP(final String value) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Create MessageSender with MessageDestinationPort,
     * InetAddress, DataGramSocket and BlockingQueue.
     * Start of MessageSenderThread.
     */
    private void initMessageSender() {
        MessageSender mss = null;
        try {
            mss = new MessageSender(MESSAGEDESTINATIONPORT,
                    iaddr, new DatagramSocket(0), bq);
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread t2 = new Thread(mss);
        t2.start();
    }

    /**
     * Create FileReceiver with COMMANDSIZE + BUFFERSIZE.
     * Start of FileReceiverThread.
     */
    private void iniFileReceiver() {
        String s = "/part";
        int slen = s.getBytes().length;
        FileReceiver fr = new FileReceiver(slen + BUFFERSIZE);
        Thread t5;
        t5 = new Thread(fr, "FileReceiverThread");
        t5.start();
    }
}
