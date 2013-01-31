/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nto
 */
public class ConsoleReader implements Runnable {

    BlockingQueue<String> bq;
    String name;
    String serverAddress;
    String receiverName;
    InetAddress iaddr;
    private Map<Enum, Command> commands = new HashMap<Enum, Command>();

    public ConsoleReader(BlockingQueue bq, InetAddress iaddr) {
        this.bq = bq;
        this.iaddr = iaddr;
        this.commands.put(ConsoleReader.CM.HELP, new Command("/help", "Type /help for command list."));
        this.commands.put(ConsoleReader.CM.NAME, new Command("/name", "Type /name <new username> to change your name."));
        this.commands.put(ConsoleReader.CM.IP, new Command("/ip", "Type /ip <new ipaddress> to change the targeted chat server."));
        this.commands.put(ConsoleReader.CM.QUIT, new Command("/quit", "Type /quit to exit the chant and termnate the program."));
        this.commands.put(ConsoleReader.CM.FILE, new Command("/file", "Type /file <targetname> <file path> to send a file."));
    }

    public void run() {
        String input;
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        while (scanner.hasNext()) {
            input = scanner.next();
            handleConsoleInput(input);
            try {
                synchronized (this.bq) {
                    bq.add("/name "+name+":"+input);
                    bq.notifyAll();
                }
            } catch (NoSuchElementException ex) {
                System.out.print("Wrong command syntax! (/command value)");
            }
        }
        scanner.close();
    }

    private InetAddress lookupIP(String value) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void initFileSender(File file, final InetAddress iaddr) {
        FileSender fs = new FileSender(receiverName, file, 9603, iaddr);
        Thread t4 = new Thread(fs);
        t4.start();
    }

    private void handleConsoleInput(final String input) throws NoSuchElementException {
        StringTokenizer st = new StringTokenizer(input);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.matches(commands.get(ConsoleReader.CM.HELP).getValue())) {
                //  showUsage();
            }
            if (token.matches(commands.get(ConsoleReader.CM.NAME).getValue())) {
                this.name = st.nextToken();
            } else if (token.matches(commands.get(ConsoleReader.CM.IP).getValue())) {
                this.serverAddress = st.nextToken();
                // startSendThread();
            } else if (token.matches(commands.get(ConsoleReader.CM.QUIT).getValue())) {
                //System.exit(0);
            } else if (token.matches(commands.get(ConsoleReader.CM.FILE).getValue())) {
                receiverName = st.nextToken();
                String path = st.nextToken();
                startFileThread(receiverName, path, this.iaddr);
            }else{
                System.out.print(this.name + ": ");
            }
        }
    }

    private void startFileThread(String receiverName, String path, InetAddress iaddr) {
        File file = new File(path);
        initFileSender(file,iaddr);
    }

    private void showUsage() {
        for (Map.Entry<Enum, Command> entry : commands.entrySet()) {
            Command value = entry.getValue();
            System.out.println(value.getUsage());
        }
    }

    private enum CM {
        NAME, IP, QUIT, FILE, HELP
    }
}
