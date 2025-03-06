import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;


public class P2P_UDPClient {
    private static final int SERVER_PORT = 9876;  // Port 9876
    private static final SecureRandom secureRandom = new SecureRandom();
    private transient DatagramSocket socket;


    public P2P_UDPClient() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Failed to create client socket: " + e.getMessage());
        }
    }


    public void sendAndReceiveMessage() {
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");  // Ensure this is your server's IP


            String message = "Hello from client";
            byte[] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);  // Port 9876
            socket.send(sendPacket);
            System.out.println("Message sent: " + message);


            byte[] buffer = new byte[1024];
            DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(incomingPacket);
            String response = new String(incomingPacket.getData(), 0, incomingPacket.getLength()).trim();
            System.out.println("Response from server: " + response);
        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
        } finally {
            socket.close();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        P2P_UDPServer2 server = new P2P_UDPServer2();
        server.start();


        TimeUnit.SECONDS.sleep(2); // Ensure server starts before client


        P2P_UDPClient client = new P2P_UDPClient();
        for (int i = 0; i < 5; i++) {
            int delay = secureRandom.nextInt(5) + 1;
            TimeUnit.SECONDS.sleep(delay);
            client.sendAndReceiveMessage();
        }


        System.out.println("Client finished sending messages.");
    }
}
