import java.io.IOException;
import java.net.*;
import java.util.*;


public class P2P_UDPClient2 {
    private DatagramSocket socket;
    private Map<InetAddress, List<String>> sentMessages; // Stores messages sent to each IP
    private Scanner scanner;


    public P2P_UDPClient2() {
        try {
            socket = new DatagramSocket();
            sentMessages = new HashMap<>();
            scanner = new Scanner(System.in);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    public void createAndListenSocket() {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1"); // Localhost
            int serverPort = 9876; // Port 9876 for server


            char ch = 'y';
            while (ch == 'y' || ch == 'Y') {
                System.out.println("Enter your message:");
                String message = scanner.nextLine();
                byte[] data = message.getBytes();


                // Send message to server on port 9876
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, serverPort);
                socket.send(sendPacket);
                storeMessage(serverAddress, message); // Store the message


                // Wait for server response
                byte[] incomingData = new byte[1024];
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                String response = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                System.out.println("Server response: " + response);


                // Ask to continue or stop
                System.out.println("Send another message? (Y/N)");
                ch = scanner.nextLine().charAt(0);
            }


            // Send termination signal
            String terminationMessage = "THEEND";
            byte[] endData = terminationMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(endData, endData.length, serverAddress, serverPort);
            socket.send(sendPacket);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void storeMessage(InetAddress address, String message) {
        sentMessages.putIfAbsent(address, new ArrayList<>());
        sentMessages.get(address).add(message);
    }


    public void printSentMessages() {
        System.out.println("\n Messages sent to the server:");
        for (Map.Entry<InetAddress, List<String>> entry : sentMessages.entrySet()) {
            System.out.println("🔹 Sent to " + entry.getKey().getHostAddress() + ":");
            for (String msg : entry.getValue()) {
                System.out.println("  ➝ " + msg);
            }
        }
    }


    public static void main(String[] args) {
        P2P_UDPClient2 client = new P2P_UDPClient2();
        client.createAndListenSocket();
    }
}


