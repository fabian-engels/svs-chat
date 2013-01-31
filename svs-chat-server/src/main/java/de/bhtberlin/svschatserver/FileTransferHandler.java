/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nto
 */
public class FileTransferHandler implements Runnable {

    Map<InetAddress, Set<String>> clients;

    FileTransferHandler(final Map<InetAddress, Set<String>> clients) {
        this.clients = clients;
    }

    public void run() {
        DatagramSocket dgs = null;
        try {
            dgs = new DatagramSocket(9603);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        DatagramPacket dgp = new DatagramPacket(new byte[1024], 1024);
        while (!Thread.currentThread().isAlive()) {
            try {
                dgs.receive(dgp);
                DatagramPacket outdgp = new DatagramPacket(dgp.getData(), dgp.getData().length);

                StringTokenizer st = new StringTokenizer(new String(dgp.getData()));
                String command = st.nextToken();
                String name = st.nextToken();

                dgp.setPort(9604);
                List<InetAddress> list;
                list = getInetAddressByName(name);
                
                Logger.getLogger(Server.class.getName()).log(Level.INFO,"Known hosts: ");
                
                for (InetAddress iaddr : list) {
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, iaddr.getHostAddress());
                    dgp.setAddress(iaddr);
                    dgs.send(dgp);
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized List<InetAddress> getInetAddressByName(String name) {
        List<InetAddress> listInet = new ArrayList<InetAddress>();
        for (InetAddress iaddr : this.clients.keySet()) {
            Set<String> names = this.clients.get(iaddr);
            String tmpname;
            for (Iterator<String> it = names.iterator(); it.hasNext();) {
                tmpname = it.next();
                if (tmpname.equals(names)) {
                    listInet.add(iaddr);
                }
            }
        }
        return listInet;
    }
}
