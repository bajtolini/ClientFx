package clientfx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable {

    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ClientThread clientThread = null;
    private String nick;
    private String message = "empty";
    private boolean finish = false;

    public Client(int serverPort, String user) {
        System.out.println("Establishing connection. Please wait ...");
        try {
            InetAddress adress = InetAddress.getLocalHost();
            socket = new Socket(adress, serverPort);
            System.out.println("Connected: " + socket);
            nick = user;
            start();
        } catch (UnknownHostException uhe) {
            System.err.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.err.println("Unexpected exception: " + ioe.getMessage());
        }
    }

    Client() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while ((thread != null) && (finish == false)) {
            try {
                if (!message.contains(".bye")) {
                    message = scanner.nextLine();
                    streamOut.writeUTF(nick + ": " + message);
                    streamOut.flush();
                } else if (message.contains(".bye")) {
                    finish = true;
                }
            } catch (IOException ioe) {
                System.err.println("Sending error: " + ioe.getMessage());
                stop();
            }
        }
    }

    public void handle(String msg) {
        if (msg.equals(".bye")) {
            System.out.println("Good bye...");
            clientThread.finish();
            finish = true;
        } else {
            System.out.println(msg);
        }
    }

    public void start() throws IOException {
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            clientThread = new ClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        try {
            if (console != null) {
                console.close();
            }
            if (streamOut != null) {
                streamOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            System.err.println("Error closing ...");
        }
        clientThread.close();
    }
}