import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author cjaiswal
 */
public class UDPClient implements Serializable {
    private static final long serialVersionUID = 1L;

    static SecureRandom secureRandom = new SecureRandom();

    // DatagramSocket is not serializable, so mark it as transient
    private transient DatagramSocket socket;
    private String clientIP;
    private int port;

    public UDPClient() {
        try {
            this.clientIP = InetAddress.getLocalHost().getHostAddress();
            this.port = 9876;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket();

            // Get the actual port assigned by the OS
            this.port = socket.getLocalPort();

            InetAddress IPAddress = InetAddress.getByName("10.111.142.78");
            byte[] incomingData = new byte[1024];

            String sentence = " ";

            File folder = new File("C:\\\\Users\\\\evanv\\\\OneDrive\\\\Computer_Science\\\\SophomoreYear\\\\CSC340\\\\Hobo_Project_1\\\\P2P");
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        sentence += listOfFiles[i];
                    }
                }
            }

            byte[] data = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            socket.send(sendPacket);
            System.out.println("Message sent from client");

            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            socket.receive(incomingPacket);

            // Extract only the actual received data
            String response = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
            System.out.println("Response from server: " + response);
            socket.close();

            // Save the updated configuration
            saveToTextFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Serializes the UDPClient object and saves it to a file.
     */
    public void saveToTextFile() {
        try (FileWriter writer = new FileWriter("P2Pconfig.txt")) {
            writer.write("Client IP: " + clientIP + "\n");
            writer.write("Port: " + port + "\n");
            System.out.println("Configuration saved as text.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads and deserializes the UDPClient object from the file.
     */
    public static UDPClient loadFromTextFile() {
        UDPClient client = new UDPClient();
        try (BufferedReader reader = new BufferedReader(new FileReader("P2Pconfig.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Client IP: ")) {
                    client.clientIP = line.substring(10).trim();
                } else if (line.startsWith("Port: ")) {
                    client.port = Integer.parseInt(line.substring(6).trim());
                }
            }
            System.out.println("Configuration loaded from text file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return client;
    }

    public void displayConfig() {
        System.out.println("Client IP: " + clientIP);
        System.out.println("Port: " + port);
    }

    public static void main(String[] args) throws InterruptedException {
        UDPClient client = new UDPClient();
        client.saveToTextFile();

        UDPClient loadedClient = UDPClient.loadFromTextFile();
        if (loadedClient != null) {
            System.out.println("Loaded from text file:");
            loadedClient.displayConfig();
        }

        // Sending messages
        int maxAttempts = 5;
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