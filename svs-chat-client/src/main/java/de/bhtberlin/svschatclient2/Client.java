/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient2;

import java.io.File;
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
    private int receivePort;
    private Thread receivThread;
    private FileSender fileSender;
    final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<String>(100);
    private Map<Enum, Command> commands = new HashMap<Enum, Command>();
    private MessageSender messageSender;
    private DatagramSocket sendSocket;
    private MessageReceiver messageReceiver;
    private DatagramSocket receiveSocket;
    private Thread sendFileThread;
    private DatagramSocket sendFileSocket;

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

    private void handleConsoleInput(final String input) {
        StringTokenizer st = new StringTokenizer(input);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.matches(commands.get(CM.NAME).getValue())) {
                this.name = st.nextToken();
            } else if (token.matches(commands.get(CM.IP).getValue())) {
                this.serverAddress = st.nextToken();
                startSendThread();
            } else if (token.matches(commands.get(CM.QUIT).getValue())) {
                System.exit(0);
            } else if (token.matches(commands.get(CM.FILE).getValue())) {
                String receiverName = st.nextToken();
                String path = st.nextToken();
                startFileThread(receiverName, path);
            } else {
                sendMessage(input);
            }
            System.out.print(this.name + ": ");
        }
    }

    private void sendMessage(final String message) {
        synchronized (this.messageQueue) {
            this.messageQueue.add(message);
            this.messageQueue.notifyAll();
        }
    }

    public Client() {
        this.name = "sven";
        this.serverAddress = "37.5.33.49";
        this.targetServerPort = 9600;
        this.receivePort = 9602;
        this.commands.put(CM.NAME, new Command("/name", "Type /name <new username> to change your name."));
        this.commands.put(CM.IP, new Command("/ip", "Type /ip <new ipaddress> to change the targeted chat server."));
        this.commands.put(CM.QUIT, new Command("/quit", "Type /quit to exit the chant and termnate the program."));
        this.commands.put(CM.FILE, new Command("/file", "Type /file <targetname> <file path> to send a file."));
        startSendThread();
        startReceiveThread();
    }

    private void startReceiveThread() {
        try {
            initReceiveSocket();
            initMessageReceiver();
            this.receivThread = new Thread(this.messageReceiver);
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.receivThread.start();
    }

    private void startSendThread() {
        /* initialize sendThread the first time */
        try {
            initSendSocket();
            initMessageSender();
            this.sendThread = new Thread(this.messageSender);
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
            this.sendThread.start();
    }

    private void initSendSocket() throws UnknownHostException, SocketException {
        this.sendSocket = new DatagramSocket(0);
    }

    private void initMessageSender() throws UnknownHostException {
        this.messageSender = new MessageSender(this.targetServerPort, InetAddress.getByName(this.serverAddress), this.sendSocket, this.messageQueue);
    }

    private void initReceiveSocket() throws SocketException {
        this.receiveSocket = new DatagramSocket(this.receivePort);
    }

    private void initMessageReceiver() {
        this.messageReceiver = new MessageReceiver(this.targetServerPort, this.sendSocket);
    }

    private void startFileThread(final String receiverName, final String path) {
        try {
            initSendFileSocket();
            final File file = new File(path);
            initFileSender(receiverName, file);
            this.sendFileThread = new Thread(this.fileSender);
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.sendFileThread.start();
    }

    private void initSendFileSocket() throws SocketException {
        this.sendFileSocket = new DatagramSocket(0);
    }

    private void initFileSender(String name, File file) throws UnknownHostException {
        this.fileSender = new FileSender(name, file, this.targetServerPort, InetAddress.getByName(this.serverAddress), this.sendSocket);
    }

    private enum CM {
        NAME, IP, QUIT, FILE
    }
}