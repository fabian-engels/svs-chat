/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient2;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nto
 */
public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        client.readStdIn();
    }
    private String name;
    private String serverAddress;
    private Thread sendThread;
    private int targetServerPort;

    private void setName(String name) {
        this.name = name;
    }

    private String getName() {
        return this.name;
    }

    private void readStdIn() {
        String input;
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        System.out.print(this.name + ": ");
        while (scanner.hasNext()) {
            input = scanner.next();
            System.out.println(input);
            handleConsoleInput(input);
        }
        scanner.close();
    }
    private Map<Enum, Command> commands = new HashMap<Enum, Command>();

    private void handleConsoleInput(final String input) {
        StringTokenizer st = new StringTokenizer(input);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (token.matches(commands.get(CM.NAME).getValue())) {
                this.name = st.nextToken();
            } else if (token.matches(commands.get(CM.IP).getValue())) {
                this.serverAddress = st.nextToken();
            } else if (token.matches(commands.get(CM.QUIT).getValue())) {
                this.sendThread.stop();
                System.exit(0);
            } else {
                System.out.print(this.name + ": ");
                sendMessage(input);
            }
        }
    }
    final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<String>(100);
    private MessageSender messageSender;
    private DatagramSocket sendSocket;

    private  void sendMessage(final String message) {
        synchronized(this.messageQueue){
            this.messageQueue.add(message);
            this.messageQueue.notifyAll();
        }
    }

    public Client() {
        this.name = "unknown";
        this.serverAddress = "127.0.0.1";
        this.targetServerPort = 9600;
        this.commands.put(CM.NAME, new Command("/name", "Type /name <new username> to change your name."));
        this.commands.put(CM.IP, new Command("/ip", "Type /ip <new ipaddress> to change the targeted chat server."));
        this.commands.put(CM.QUIT, new Command("/quit", "Type /quit to exit the chant and termnate the program."));
        
        try {
            initSendSocket();
            initMessageSender();
            this.sendThread = new Thread(this.messageSender);
            this.sendThread.start();
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }catch (UnknownHostException ex){
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void initSendSocket() throws UnknownHostException, SocketException {
            this.sendSocket = new DatagramSocket(0, InetAddress.getLocalHost());
    }

    private void initMessageSender() throws UnknownHostException {
        this.messageSender = new MessageSender(this.targetServerPort, InetAddress.getByName(this.serverAddress), this.sendSocket, this.messageQueue);
    }

    private enum CM {
        NAME, IP, QUIT
    }
}