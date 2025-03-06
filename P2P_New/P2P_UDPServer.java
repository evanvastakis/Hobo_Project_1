import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

/**
 * 
 * @author cjaiswal
 *
 */
public class P2P_UDPServer {
    private DatagramSocket socket = null;
    private HashMap<InetAddress, Integer> nodeMap = new HashMap<>();  // HashMap to store client IP and port

    public P2P_UDPServer() {
        // Constructor, socket initialization happens in createAndListenSocket
    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket(9876);  // Listen on port 9876
            byte[] incomingData = new byte[1024];  // Buffer for incoming packets

            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);  // Receive incoming packet

                // Retrieve client message, IP address, and port
                String message = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();

                // Print received message and client info
                System.out.println("Received message from client: " + message);
                System.out.println("Client IP: " + IPAddress.getHostAddress());
                System.out.println("Client port: " + port);

                // Store or update client information in the HashMap
                if (!nodeMap.containsKey(IPAddress)) {
                    nodeMap.put(IPAddress, port);  // Add new client to the map
                    System.out.println("New node added: " + IPAddress.getHostAddress() + " with port " + port);
                }

                // Print the list of current nodes in the network
                System.out.println("Current nodes in the network:");
                for (InetAddress node : nodeMap.keySet()) {
                    System.out.println("Node IP: " + node.getHostAddress() + ", Port: " + nodeMap.get(node));
                }

                // Send reply to the client
                String reply = "Thank you for the message";
                byte[] data = reply.getBytes();
                DatagramPacket replyPacket = new DatagramPacket(data, data.length, IPAddress, port);
                socket.send(replyPacket);

                // Simulate a brief delay
                Thread.sleep(2000);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Ensure socket is closed properly
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    public static void main(String[] args) {
        P2P_UDPServer server = new P2P_UDPServer();
        server.createAndListenSocket();
    }
}
