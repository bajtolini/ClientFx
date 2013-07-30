package clientfx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ClientThread clientThread = null;
    private String nick;
    private boolean finish = false;
    private ClientFx clientfx;

    public Client(int serverPort, String user, ClientFx _clientfx) {
        System.out.println("Establishing connection. Please wait ...");
        try {
            InetAddress adress = InetAddress.getLocalHost();
            socket = new Socket(adress, serverPort);
            System.out.println("Connected: " + socket);
            nick = user;
            clientfx = _clientfx;
            start();
        } catch (UnknownHostException uhe) {
            System.err.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.err.println("Unexpected exception: " + ioe.getMessage());
        }
    }

    @Override
    public void run() {
    }

    public void start() throws IOException {
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            clientThread = new ClientThread(this, socket, clientfx);
        }
    }

    public void stop() {
        clientThread.finish();
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

    public void send(String newmessage) {
        try {
            streamOut.writeUTF(nick + ": " + newmessage);
            streamOut.flush();
        } catch (IOException ioe) {
            System.err.println("Sending error: " + ioe.getMessage());
            stop();
        }
    }
}