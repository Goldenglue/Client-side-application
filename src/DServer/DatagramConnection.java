package DServer;

import nicepackage.Board;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by IvanOP on 19.05.2017.
 */
public class DatagramConnection {
    private static DatagramExecutor executor;
    private static DatagramSocket socketInput;
    private static DatagramSocket socketOutput;
    private static DatagramPacket packetOfData;
    private static ByteArrayOutputStream byteArrayOutputStream;
    private static InetAddress address;
    private static int inputPortNumber = 4021;
    private static int outputPortNumber = 4020;
    private boolean isConnected = false;
    private static byte[] bufferForData = new byte[256];
    private Thread runConnectionThread;

    private Runnable runConnection = () -> {
        while (isConnected) {
            String[] receivedData = receivePacketOfData();
            System.out.println(receivedData[1]);
            executor.executeMessageFromClient(receivedData);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public DatagramConnection(Board board) {
        executor = new DatagramExecutor(board);
        try {
            address = InetAddress.getByName("localhost");
            socketInput = new DatagramSocket(inputPortNumber, address);
            socketOutput = new DatagramSocket();
            isConnected = true;
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        runConnectionThread = new Thread(runConnection);
        runConnectionThread.start();
    }

    static void sendPacketOfData(String data) {
        bufferForData = data.getBytes();
        System.out.println("sending this many bytes: " + bufferForData.length);
        if (bufferForData.length > 256) {
            int offset = 0;
            while (offset < bufferForData.length) {
                byte[] tempArray;
                if (offset + 256 < bufferForData.length) {
                    tempArray = Arrays.copyOfRange(bufferForData, offset, offset + 256);
                    offset += 256;
                } else {
                    tempArray = Arrays.copyOfRange(bufferForData, offset, bufferForData.length);
                    offset += 256;
                }
                System.out.println("sending " + new String(tempArray) );
                packetOfData = new DatagramPacket(tempArray, tempArray.length, address, outputPortNumber);
                try {
                    socketOutput.send(packetOfData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            packetOfData = new DatagramPacket(bufferForData, bufferForData.length, address, outputPortNumber);
            System.out.println("sending " + new String(bufferForData));
            try {
                socketOutput.send(packetOfData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*String endString = "end";
        bufferForData = endString.getBytes();
        packetOfData = new DatagramPacket(bufferForData, bufferForData.length, address, outputPortNumber);
        try {
            socketOutput.send(packetOfData);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static String[] receivePacketOfData() {
        packetOfData = new DatagramPacket(bufferForData, bufferForData.length);
        String[] everythingThatIsNeeded = new String[2];
        String whateverCame = null;
        String command = null;
        StringBuilder builder = new StringBuilder();
        try {
            socketInput.receive(packetOfData);
            command = new String(packetOfData.getData());
            command = command.substring(0,6);
            System.out.println("command is " + command);
            while (true) {
                packetOfData = new DatagramPacket(bufferForData, bufferForData.length);
                socketInput.receive(packetOfData);
                String receivedData = new String(packetOfData.getData());
                System.out.println(receivedData);
                if (Objects.equals(receivedData.substring(0,3), "end")) {
                    System.out.println("ending");
                    break;
                } else {
                    builder.append(receivedData);
                }
            }
            whateverCame = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        everythingThatIsNeeded[0] = command;
        everythingThatIsNeeded[1] = whateverCame;
        return everythingThatIsNeeded;
    }

    public static void processCommand(String clientCommand) {
        executor.executeMessageFromClient(clientCommand);
    }

}
