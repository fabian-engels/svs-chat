/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nto
 */
public class Main {
    
   public static void main (String args[]){
       
       BlockingQueue<String> bq = new ArrayBlockingQueue<String> (1000);
       MessageSender mss = null;
        try {
           try {
                mss = new MessageSender(9600, InetAddress.getByName("85.178.207.95"), new DatagramSocket(0), bq);
           } catch (UnknownHostException ex) {
               Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
           }
        } catch (SocketException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Thread t2 = new Thread(mss);
        t2.start();
               
    MessageReceiver msr = new MessageReceiver(1024);
    Thread t1 = new Thread(msr);
    t1.start();
        
    ConsoleReader cr = new ConsoleReader(bq);
    Thread t3 = new Thread(cr);
    t3.start();
    
    }
   
  
}
