/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient;

import java.io.File;
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
public class Main {

    public static void main(String args[]) {
        Main main = new Main();
        main.init();
    }
    BlockingQueue<String> bq = new ArrayBlockingQueue<String>(1000);
    InetAddress iaddr;

    private void init() {
        iaddr = lookupIP("37.5.33.49");
        
        this.initMessageReceiver();
        this.iniFileReceiver();
        this.initMessageSender();
        this.initConsoleReader();
    }

    private void initConsoleReader() {
        ConsoleReader cr = new ConsoleReader(bq, iaddr);
        Thread t3 = new Thread(cr);
        t3.start();
    }

    private void initMessageReceiver() {
        MessageReceiver msr = new MessageReceiver(1024);
        Thread t1 = new Thread(msr);
        t1.start();
    }

    private InetAddress lookupIP(String value) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void initMessageSender() {
        MessageSender mss = null;
        try {
            mss = new MessageSender(9600, iaddr, new DatagramSocket(0), bq);
        } catch (SocketException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread t2 = new Thread(mss);
        t2.start();
    }

    private void iniFileReceiver() {
        FileReceiver fr = new FileReceiver(1024);
        Thread t5 = new Thread(fr);
        t5.start();
    }
}
