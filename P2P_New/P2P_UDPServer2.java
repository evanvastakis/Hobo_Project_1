import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class P2P_UDPServer2 extends Thread {
    private static final int DEFAULT_PORT = 9876;
    // private static final int TIMEOUT_SECONDS = 8;


    private DatagramSocket socket;
    // private ConcurrentHashMap<String, InetAddress> nodeIPS = new ConcurrentHashMap<>();
    // private ConcurrentHashMap<InetAddress, Integer> nodePorts = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<String>> nodeMessages = new ConcurrentHashMap<>(); // Store messages per IP
    // private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(6);
    // private ConcurrentHashMap<String, ScheduledFuture<?>> nodeTimers = new ConcurrentHashMap<>();


    public P2P_UDPServer2() {
        try {
            socket = new DatagramSocket(DEFAULT_PORT);
            System.out.println("Server started on port: " + socket.getLocalPort());
        } catch (SocketException e) {
            System.err.println("Port " + DEFAULT_PORT + " is in use. Trying a random available port...");
            try {
                socket = new DatagramSocket(0);
                System.out.println("Server started on port: " + socket.getLocalPort());
            } catch (SocketException ex) {
                System.err.println("Failed to start server: " + ex.getMessage());
                System.exit(1);
            }
        }
    }


    public void createAndListenSocket() {
        byte[] incomingData = new byte[1024];


        while (true) {
            try {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                System.out.println("Waiting for messages...");
                socket.receive(incomingPacket);


                InetAddress clientAddress = incomingPacket.getAddress();
                int clientPort = incomingPacket.getPort();
                String message = new String(incomingPacket.getData(), 0, incomingPacket.getLength()).trim();


                System.out.println("\nReceived from " + clientAddress + ":" + clientPort + " -> " + message);


                // Store the message in the hashmap
                storeMessage(clientAddress, message);


                // Acknowledge message reception
                String reply = "Message received: " + message;
                DatagramPacket replyPacket = new DatagramPacket(reply.getBytes(), reply.length(), clientAddress, clientPort);
                socket.send(replyPacket);


            } catch (IOException e) {
                System.err.println("Error receiving message: " + e.getMessage());
            }
        }
    }


    private void storeMessage(InetAddress clientAddress, String message) {
        String clientKey = clientAddress.getHostAddress(); // Use the IP address as the key
        nodeMessages.putIfAbsent(clientKey, new ArrayList<>());
        nodeMessages.get(clientKey).add(message); // Add the message to the list of messages for this IP


        // Print messages from the IP
        System.out.println("\nMessages sent by " + clientKey + ":");
        for (String msg : nodeMessages.get(clientKey)) {
            System.out.println("  ➝ " + msg);
        }
    }


    public static void main(String[] args) {
        P2P_UDPServer2 server = new P2P_UDPServer2();
        server.createAndListenSocket();
    }
}


