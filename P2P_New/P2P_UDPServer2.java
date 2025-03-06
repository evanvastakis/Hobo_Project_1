import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class P2P_UDPServer2 {
    private DatagramSocket socket = null;
    private HashMap<InetAddress, Integer> nodeMap = new HashMap<>();  // HashMap to store nodes' IP and port

    public P2P_UDPServer2() {
        try {
            // Create the socket assuming the server is listening on port 9876
            socket = new DatagramSocket(9876);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void createAndListenSocket() {
        try {
            // Incoming data buffer
            byte[] incomingData = new byte[1024];

            while (true) {
                // Create incoming packet
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                System.out.println("Waiting...");

                // Wait for the packet to arrive and store it in incoming packet
                socket.receive(incomingPacket);

                // Retrieve the data from the packet
                String message = new String(incomingPacket.getData(), 0, incomingPacket.getLength());

                // Terminate if it is the "THEEND" message from the client
                if (message.equals("THEEND")) {
                    socket.close();
                    break;
                }

                // Print the received message and client details
                System.out.println("Received message from client: " + message);
                System.out.println("Client Details: PORT " + incomingPacket.getPort() + ", IP Address: " + incomingPacket.getAddress());

                // Store or update client in the HashMap
                InetAddress clientAddress = incomingPacket.getAddress();
                int clientPort = incomingPacket.getPort();

                // If the node is not in the map, add it
                if (!nodeMap.containsKey(clientAddress)) {
                    nodeMap.put(clientAddress, clientPort);
                    System.out.println("New node added: " + clientAddress + " with port " + clientPort);
                }

                // Print the current nodes in the network
                System.out.println("Current Nodes in the Network:");
                for (InetAddress address : nodeMap.keySet()) {
                    System.out.println("Node IP: " + address + ", Port: " + nodeMap.get(address));
                }

                // Send a reply to the client
                String reply = "Thank you for the message";
                byte[] data = reply.getBytes();
                DatagramPacket replyPacket = new DatagramPacket(data, data.length, clientAddress, clientPort);
                socket.send(replyPacket);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static void main(String[] args) {
        P2P_UDPServer2 server = new P2P_UDPServer2();
        server.createAndListenSocket();
    }
}
