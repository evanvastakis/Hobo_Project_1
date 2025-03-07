import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class P2P_UDPClient extends P2P_Protocol implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int serverPort = 9876;

    String evanIP = "10.111.134.82";
    String grantIP = "10.111.142.78";
    String jessIP = "10.111.152.150";
    String vm1IP = "";
    String vm2IP = "";
    String vm3IP = "";

    String[] IPS = {evanIP, grantIP, jessIP, vm1IP, vm2IP, vm3IP};

    static SecureRandom secureRandom = new SecureRandom();

    // DatagramSocket is not serializable, so mark it as transient
    private transient DatagramSocket socket;

    public P2P_UDPClient() {}

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket(serverPort);
            socket.setSoTimeout(5000); // Set a 5-second timeout
            serverPort++;

            for(int currentNode = 0; currentNode < 6; currentNode++) {

                // if(!IPS[currentNode].equals()){
                //     this.setDestIp(InetAddress.getByName(IPS[currentNode]));
                // }

                // this.setDestIp(InetAddress.getByName(IPS[currentNode]));  // Destination IP (to be written in from config) EVANS: 10.111.142.78  MINE: 10.111.134.82
                this.setDestPort(9876);

                byte[] incomingData = new byte[1024];
                String sentence = " ";

                // Evan
                // File folder = new File("C:\\Users\\evanv\\OneDrive\\Computer_Science\\SophomoreYear\\CSC340\\Hobo_Project_1\\C2S_New");
                
                // Grant
                File folder = new File("/Users/grant/Downloads/Course Materials/Spring 2025/CSC340/Hobo_Project_1/C2S_New");
                
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
                this.setDestIp(InetAddress.getByName(IPS[currentNode])); // Ensure IP is set
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, getDestIp(), serverPort);
                socket.send(sendPacket);
                System.out.println("Message sent to: " + IPS[currentNode]);
                
                // Receiving (response)
                // DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                // socket.receive(incomingPacket);

                // Extract only the actual received data
                // String response = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                // System.out.println("Response from server: " + response);

                // Save the updated configuration
                // saveToTextFile();

                System.out.println("-----------------------");

                // Receive server's file listing 
                System.out.println("Other Client's File Listings (From Server):");
                byte[] fileLists = new byte[2048];
                DatagramPacket fileListsPacket = new DatagramPacket(fileLists, fileLists.length);

                try {
                    socket.receive(fileListsPacket);
                    String fileListsString = new String(fileListsPacket.getData(), 0, fileLists.length);
                    System.out.println(fileListsString);    
                } catch(SocketTimeoutException e) {
                    System.out.println("Other clients offline. Skipping file list retrieval.");
                }

                System.out.println("----------------------------------------------");
                }    
            }
        catch (Exception e) {
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

    public static void main(String[] args) throws InterruptedException {
        P2P_UDPClient client = new P2P_UDPClient();
        // Sending messages
        int maxAttempts = 20;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int random = secureRandom.nextInt(5) + 1; // Bound: max time between heartbeats
            System.out.println("Waiting for " + random + " seconds...\n");
            TimeUnit.SECONDS.sleep(random);
            client.createAndListenSocket();
        }

        System.out.println("Max attempts reached. Exiting.");
    }
}