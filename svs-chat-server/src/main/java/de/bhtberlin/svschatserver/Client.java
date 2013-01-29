/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatserver;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author nto
 */
public class Client {
    InetAddress inetAddress;
    Set<String> names;

    public Client(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
            this.names = new HashSet<String>();
    }
    
    public void addName(String name) {
        this.names.add(name);
    }
    
    public void removeName(String name){
        this.names.remove(name);
    }
}
