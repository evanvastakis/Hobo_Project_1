import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class P2P_UDPClient extends P2P_Protocol implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int serverPort = 9876;

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

    public P2P_UDPClient() {

    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket(9876);
            socket.setSoTimeout(5000); // Set a 5-second timeout

            for(int currentNode = 0; currentNode < 6; currentNode++) {

                if(!IPS[currentNode].equals()){
                    this.setDestIp(InetAddress.getByName(IPS[currentNode]));
                }

                // this.setDestIp(InetAddress.getByName(IPS[currentNode]));  // Destination IP (to be written in from config) EVANS: 10.111.142.78  MINE: 10.111.134.82
                this.setDestPort(9876);

                byte[] incomingData = new byte[1024];
                String sentence = " ";

                // Evan
                File folder = new File("C:\\Users\\evanv\\OneDrive\\Computer_Science\\SophomoreYear\\CSC340\\Hobo_Project_1\\C2S_New");
                
                // Grant
                // File folder = new File("/Users/grant/Downloads/Course Materials/Spring 2025/CSC340/Hobo_Project_1/C2S_New");
                
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
                for(int i = 0; i < 6; i++) {
                    DatagramPacket sendPacket = new DatagramPacket(data, data.length, getDestIp(), serverPort);
                    socket.send(sendPacket);
                    System.out.println("Message sent.\n");
                }

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

    public void displayConfig() {
        // System.out.println("Client IP: " + getIP());
        // System.out.println("Port: " + port);
    }

    public static void main(String[] args) throws InterruptedException {
        P2P_UDPClient client = new P2P_UDPClient();
        // P2P_UDPServer2 server = new P2P_UDPServer2();

        // server.run();

        // Sending messages
        int maxAttempts = 20;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int random = secureRandom.nextInt(30) + 1; // Bound: max time between heartbeats
            System.out.println("Waiting for " + random + " seconds...\n");
            TimeUnit.SECONDS.sleep(random);
            client.createAndListenSocket();
        }

        System.out.println("Max attempts reached. Exiting.");
    }
}

// class P2P_UDPServer2 extends Thread {
    
//     final int NODE_COUNT = 6;
//     private static final int TIMEOUT_SECONDS = 8;

//     private boolean[] nodeStatus = new boolean[NODE_COUNT];
    
//     // We use the String array to get or store each node's IP address
//     private HashMap<String, InetAddress> nodeIPS = new HashMap<String, InetAddress>();
//     private String[] IPKeys = {"node1", "node2", "node3", "node4", "node5", "node6"};
    
//     // We will be using the constant IP addresses of the nodes to track of the changing port numbers of the nodes
//     private HashMap<InetAddress, Integer> nodePorts = new HashMap<InetAddress, Integer>(); 

//     private DatagramSocket socket = null;

//     // Timers
//     private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(NODE_COUNT);
//     private final HashMap<String, ScheduledFuture<?>> nodeTimers = new HashMap<>();

//     public P2P_UDPServer2() {
//     	try {
//     		//create the socket assuming the server is listening on port 9876
// 			socket = new DatagramSocket(9876);

//             // By default, store all 
//             for (String key : IPKeys) {
//                 nodeIPS.put(key, null);
//             }

// 		} 
//     	catch (SocketException e) {
// 			e.printStackTrace();
// 		}

//     }

//     public void createAndListenSocket() {

//         try {

//         	//incoming data buffer
//             byte[] incomingData = new byte[1024];

//             while (true) {

//             	// Create incoming packet
//                 DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
//                 System.out.println("Waiting...");
//                 // Wait for the packet to arrive and store it in incoming packet
//                 socket.receive(incomingPacket);

//                 // Store client details in variables 
//                 InetAddress clientAddress = incomingPacket.getAddress();
//                 int clientPort = incomingPacket.getPort();
//                 String message = new String(incomingPacket.getData(), 0, incomingPacket.getLength());

//                 // Save client config
//                 try (FileWriter writer = new FileWriter("P2Pconfig.txt")) {
//                     writer.write(clientAddress + "\n");
//                     writer.write(clientPort + "\n");
//                     System.out.println("Updated config.");
//                 } catch (IOException e) {
//                     e.printStackTrace();
//                 }

//                 // Update node status and restart countdown timer
//                 updateNodeStatus(clientAddress);


//                 // Fills up the nodeIPS hash map with updated IP values
//                 for (int i = 0; i < NODE_COUNT; i++) {
//                     InetAddress currentAddress = nodeIPS.get(IPKeys[i]);
//                     int currentPort = 0;

//                     // Only replace the slot if it is not null and the incoming IP is not already in the map
//                     if (currentAddress == null && !nodeIPS.containsValue(clientAddress)) {
//                         nodeIPS.put(IPKeys[i], clientAddress);
//                         break; // Exit loop after inserting the new address
//                     }

//                     // only replace the slot if it is not zero and the incoming Port is not already in the map
//                     if (currentPort == 0 && !nodePorts.containsValue(clientPort)) {
//                         nodePorts.put(nodeIPS.get(IPKeys[i]), clientPort);
//                         break;
//                     }
//                     // Print updated IPs and Ports
//                     System.out.println(nodeIPS.get(IPKeys[i]));
//                     System.out.println(nodePorts.get(nodeIPS.get(IPKeys[i])));
//                 }

//                 //terminate if it is "THEEND" message from the client
//                 // if(message.equals("THEEND")) {
//                 // 	socket.close();
//                 // 	break;
//                 // }

//                 System.out.println("\nMessage recieved: " + message);
//                 System.out.println("Client Details: Port: " + incomingPacket.getPort() + ", IP Address:" + incomingPacket.getAddress());
                
//                 // turn the message into a byte stream to send to the other nodes
//                 byte[] data = message.getBytes();

//                 // This piece of code will send the message of this client node to all the other nodes that are not his
//                 for(int i = 0; i < NODE_COUNT; i++){
//                     if(!nodeIPS.get(IPKeys[i]).equals(clientAddress)){
//                         DatagramPacket nodeReplyPacket = new DatagramPacket(data, message.length(), nodeIPS.get(IPKeys[i]), nodePorts.get(nodeIPS.get(IPKeys[i])));
//                         socket.send(nodeReplyPacket);
//                         System.out.println("Send message to " + IPKeys[i]);
//                     }
//                 }

//                 // Create response packet to the node that sent the message               
//                 String reply = "Thank you for the message";
//                 byte[] responseData = reply.getBytes();
//                 DatagramPacket replyPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
//                 socket.send(replyPacket);
//             }
//         } 
//         catch (SocketException e) {
//             e.printStackTrace();
//         } 
//         catch (IOException i) {
//             i.printStackTrace();
//         } 
//     }

//     public void updateNodeStatus(InetAddress clientAddress){
//         // How we will make sure that the node is up or down
//         for(int i = 0; i < NODE_COUNT; i++){

//             // match incoming packet to its status 
//             if(nodeIPS.get(IPKeys[i]) != null && nodeIPS.get(IPKeys[i]).equals(clientAddress)){
//                 nodeStatus[i] = true;
//                 System.out.println("\nNode " + (i+1) + " is alive: " + nodeStatus[i] + "\n");

//                 // Restart the countdown timer for this node
//                 resetCountdownTimer(IPKeys[i]);
//                 return;
//             }
//         }                
//     }

//     private void resetCountdownTimer(String nodeKey) {
//         // Cancel the previous timer for this node, if it exists
//         if (nodeTimers.containsKey(nodeKey)) {
//             nodeTimers.get(nodeKey).cancel(false);  // Only cancels this node's timer
//         }
    
//         // Define the timeout task (after timer ends)
//         Runnable timeoutTask = () -> {
//             for (int i = 0; i < NODE_COUNT; i++) {
//                 if (IPKeys[i].equals(nodeKey)) {
//                     nodeIPS.put(nodeKey, null);
//                     nodeStatus[i] = false;
//                     System.out.println("\nNode " + (i + 1) + " has gone offline.\n");

//                     // Print updated list with offline node
//                     for (int j = 0; j < NODE_COUNT; j++) {
//                         System.out.println(nodeIPS.get(IPKeys[j]));
//                     }
//                 } 
//             }
//         };
    
//         // Schedule the task and store its reference
//         ScheduledFuture<?> future = scheduler.schedule(timeoutTask, TIMEOUT_SECONDS, TimeUnit.SECONDS);
//         nodeTimers.put(nodeKey, future);
//     }
    
//     public static void main(String[] args) {
//         P2P_UDPServer2 server = new P2P_UDPServer2();
//         server.createAndListenSocket();
//     }

// }