/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient;

import java.net.DatagramSocket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author nto
 */
public class ConsoleReader implements Runnable {

    BlockingQueue<String> bq;
    
    public ConsoleReader(BlockingQueue bq) {
        this.bq = bq;
    }
    
    public void run() {
        String input;
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        while (scanner.hasNext()) {
            input = scanner.next();
            try {
                synchronized(this.bq){
                bq.add(input);
                bq.notifyAll();
                }
            } catch (NoSuchElementException ex) {
                System.out.print("Wrong command syntax! (/command value)");
            }
        }
        scanner.close();
    }
    
}
