package de.bhtberlin.svschatclient;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Hello world!
 *
 */
public class Client 
{
    public static void main( String[] args ) throws SocketException
    {
        DatagramSocket s = new DatagramSocket(8888);
        
    }
}