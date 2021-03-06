package SServer;

import nicepackage.Board;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Created by IvanOP on 04.05.2017.
 */
public class ClientSideConnection {
    Socket socket;
    InetAddress host;
    int serverPort = 4000;
    PrintWriter toServer;
    BufferedReader fromServer;
    boolean isConnected = false;
    private boolean isDisconnected = false;
    private Thread clientSideConnectionThread;
    private ClientServerExecutor clientServerExecutor;


    private Runnable runClientSideConnection = () -> {
        while (true) {
            try {
                sleep(50);
                synchronized (this) {
                    while (isDisconnected) {
                        wait();
                    }
                }
                clientServerExecutor.executeMessageFromClient(getMessage());
                while (isConnected) {
                    String line = fromServer.readLine();
                    if (!line.equals("")) {
                        System.out.println("Client received " + line);
                    }
                    clientServerExecutor.executeMessageFromClient(line);
                    clientServerExecutor.executeMessageFromClient(getMessage());
                    toServer.println("");
                    toServer.flush();
                    sleep(50);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public ClientSideConnection(Board board) {
        clientServerExecutor = new ClientServerExecutor(this, board);
        startThread();

    }

    private String getMessage() {
        return null;
    }

    private void startThread() {
        this.clientSideConnectionThread = new Thread(runClientSideConnection);
        this.clientSideConnectionThread.start();
    }

    synchronized void stopThread() {
        this.isDisconnected = true;
    }

    private synchronized void resumeThread() {
        this.isDisconnected = false;
        this.notify();
    }
}
