import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author cjaiswal
 */
public class UDPClient extends Protocol implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int serverPort = 9876;

    static SecureRandom secureRandom = new SecureRandom();

    // DatagramSocket is not serializable, so mark it as transient
    private transient DatagramSocket socket;

    public UDPClient() {
        // try {
        //     // Get own IP
        //     InetAddress ip = InetAddress.getLocalHost();
        //     String ipAddress = ip.getHostAddress(); // Get IP as a string
        //     System.out.println("DEBUG, OWN IP: " + ipAddress.toString());
        // } catch(UnknownHostException e) {
        
        // }
    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket();

            this.setDestIp(InetAddress.getByName("localhost"));  // Destination IP (to be written in from config) EVANS: 10.111.142.78
            this.setDestPort(9876);

            byte[] incomingData = new byte[1024];
            String sentence = " ";

            // Evan
            File folder = new File("C:\\Users\\evanv\\OneDrive\\Computer_Science\\SophomoreYear\\CSC340\\Hobo_Project_1\\P2P");
            
            // Grant
            // File folder = new File("/Users/grant/Downloads/Course Materials/Spring 2025/CSC340/Hobo_Project_1/P2P");
            
            File[] listOfFiles = folder.listFiles();

            if (listOfFiles != null) {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        sentence += listOfFiles[i].getName() + ", "; // Add file name with a space separator
                    }
                }
            }

            // Sending
            byte[] data = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, getDestIp(), serverPort);
            socket.send(sendPacket);
            System.out.println("Message sent from client");

            // Receiving (response)
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            socket.receive(incomingPacket);

            // Extract only the actual received data
            String response = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
            System.out.println("Response from server: " + response);
            socket.close();

            // Save the updated configuration
            // saveToTextFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles object serialization.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Handles object deserialization and reinitializes transient fields.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        socket = new DatagramSocket(); // Reinitialize transient field
    }

    public void displayConfig() {
        // System.out.println("Client IP: " + getIP());
        // System.out.println("Port: " + port);
    }

    public static void main(String[] args) throws InterruptedException {
        UDPClient client = new UDPClient();
        
        // Sending messages
        int maxAttempts = 20;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int random = secureRandom.nextInt(5) + 1;
            System.out.println("Waiting for " + random + " seconds");
            TimeUnit.SECONDS.sleep(random);
            System.out.println("We are waiting");
            client.createAndListenSocket();
        }

        System.out.println("Max attempts reached. Exiting.");
    }
}
