package nicepackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Created by IvanOP on 19.05.2017.
 */
public class DatagramConnection {
    static DatagramExecutor executor;
    private static DatagramSocket socketInput;
    private static DatagramSocket socketOutput;
    private static DatagramPacket packetOfDataSize;
    private static DatagramPacket packetOfData;
    private static ByteArrayOutputStream byteArrayOutputStream;
    private static InetAddress address;
    private static int inputPortNumber = 4021;
    private static int outputPortNumber = 4020;
    private boolean isConnected = false;
    private static byte[] bufferForDataSize = new byte[4];
    private static byte[] bufferForData;
    private Thread runConnectionThread;

    private Runnable runConnection = () -> {
        while (isConnected) {
            String data = receivePacketOfData();
            System.out.println(data);
            executor.executeMessageFromClient(data);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    DatagramConnection(Board board) {
         executor = new DatagramExecutor(board);
        try {
            address = InetAddress.getByName("localhost");
            socketInput = new DatagramSocket(inputPortNumber,address);
            socketOutput = new DatagramSocket();
            isConnected = true;
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        String onConnection = "New client has arrived!";
        sendPacketOfData(onConnection);

        runConnectionThread = new Thread(runConnection);
        runConnectionThread.start();
    }

    private static void sendPacketOfDataSize(String data) {
        int size = data.length();
        bufferForDataSize = ByteBuffer.allocate(4).putInt(size).array();
        packetOfDataSize = new DatagramPacket(bufferForDataSize, bufferForDataSize.length, address, outputPortNumber);
        try {
            socketOutput.send(packetOfDataSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendPacketOfData(String data) {
        sendPacketOfDataSize(data);
        System.out.println("sending: " + data);
        bufferForData = data.getBytes();
        packetOfData = new DatagramPacket(bufferForData, bufferForData.length, address, outputPortNumber);
        try {
            socketOutput.send(packetOfData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int receivePacketOfDataSize() {
        packetOfDataSize = new DatagramPacket(bufferForDataSize, bufferForDataSize.length);
        try {
            socketInput.receive(packetOfDataSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(packetOfDataSize.getData()).getInt();
    }

    static String receivePacketOfData() {
        int size = receivePacketOfDataSize();
        System.out.println("size of packet: " + size);
        bufferForData = new byte[size];
        packetOfData = new DatagramPacket(bufferForData, bufferForData.length);
        try {
            socketInput.receive(packetOfData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packetOfData.getData());
    }

    static void processCommand(String clientCommand) {
        executor.executeMessageFromClient(clientCommand);
    }

}
