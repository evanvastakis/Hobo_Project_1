import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class UDPServer2 {
    
    final int NODE_COUNT = 6;
    private static final int TIMEOUT_SECONDS = 8;

    private boolean[] nodeStatus = new boolean[NODE_COUNT];
    private HashMap<String, InetAddress> nodeIPS = new HashMap<String, InetAddress>();
    private String[] IPKeys = {"node1", "node2", "node3", "node4", "node5", "node6"};
    // private int[] nodeTimes = new int[NODE_COUNT];

    private DatagramSocket socket = null;

    // Timers
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(NODE_COUNT);
    private final HashMap<String, ScheduledFuture<?>> nodeTimers = new HashMap<>();

    public UDPServer2() {
    	try {
    		//create the socket assuming the server is listening on port 9876
			socket = new DatagramSocket(9876);

            // By default, store all 
            for (String key : IPKeys) {
                nodeIPS.put(key, null);
            }

		} 
    	catch (SocketException e) {
			e.printStackTrace();
		}

    }

    public void createAndListenSocket() {

        try {

        	//incoming data buffer
            byte[] incomingData = new byte[1024];

            while (true) {

            	// Create incoming packet
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                System.out.println("Waiting...");
                // Wait for the packet to arrive and store it in incoming packet
                socket.receive(incomingPacket);

                // Store client details in variables 
                InetAddress clientAddress = incomingPacket.getAddress();
                int clientPort = incomingPacket.getPort();
                String message = new String(incomingPacket.getData(), 0, incomingPacket.getLength());

                // Save client config
                try (FileWriter writer = new FileWriter("P2Pconfig.txt")) {
                    writer.write("Client IP: " + incomingPacket.getAddress() + "\n");
                    writer.write("Port: " + incomingPacket.getPort() + "\n");
                    System.out.println("Updated config.");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Update node status and restart countdown timer
                updateNodeStatus(clientAddress);


                // Fills up the hash map with updated IP values
                for (int i = 0; i < NODE_COUNT; i++) {
                    InetAddress currentAddress = nodeIPS.get(IPKeys[i]);
                
                    // Only replace the slot if it matches the local IP and the incoming IP is not already in the map
                    if (currentAddress == null && !nodeIPS.containsValue(incomingPacket.getAddress())) {
                        nodeIPS.put(IPKeys[i], incomingPacket.getAddress());
                        break; // Exit loop after inserting the new address
                    }

                    // Print updated IPs
                    System.out.println(nodeIPS.get(IPKeys[i]));

                }
                            
                //terminate if it is "THEEND" message from the client
                if(message.equals("THEEND")) {
                	socket.close();
                	break;
                }

                System.out.println("\nMessage recieved: " + message);
                System.out.println("Client Details: Port: " + incomingPacket.getPort() + ", IP Address:" + incomingPacket.getAddress());
                
                // Create response packet                
                String reply = "Thank you for the message";
                byte[] responseData = reply.getBytes();
                DatagramPacket replyPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                socket.send(replyPacket);
            }
        } 
        catch (SocketException e) {
            e.printStackTrace();
        } 
        catch (IOException i) {
            i.printStackTrace();
        } 
    }

    public void updateNodeStatus(InetAddress clientAddress){
        // How we will make sure that the node is up or down
        for(int i = 0; i < NODE_COUNT; i++){

            // match incoming packet to its status 
            if(nodeIPS.get(IPKeys[i]) != null && nodeIPS.get(IPKeys[i]).equals(clientAddress)){
                nodeStatus[i] = true;
                System.out.println("\nNode " + (i+1) + " is alive: " + nodeStatus[i] + "\n");

                // Restart the countdown timer for this node
                resetCountdownTimer(IPKeys[i]);
                return;
            }
        }                
    }

    private void resetCountdownTimer(String nodeKey) {
        // Cancel the previous timer for this node, if it exists
        if (nodeTimers.containsKey(nodeKey)) {
            nodeTimers.get(nodeKey).cancel(false);  // Only cancels this node's timer
        }
    
        // Define the timeout task (after timer ends)
        Runnable timeoutTask = () -> {
            for (int i = 0; i < NODE_COUNT; i++) {
                if (IPKeys[i].equals(nodeKey)) {
                    nodeIPS.put(nodeKey, null);
                    nodeStatus[i] = false;
                    System.out.println("\nNode " + (i + 1) + " has gone offline.\n");

                    // Print updated list with offline node
                    for (int j = 0; j < NODE_COUNT; j++) {
                        System.out.println(nodeIPS.get(IPKeys[j]));
                    }
                } 
            }
        };
    
        // Schedule the task and store its reference
        ScheduledFuture<?> future = scheduler.schedule(timeoutTask, TIMEOUT_SECONDS, TimeUnit.SECONDS);
        nodeTimers.put(nodeKey, future);
    }
    
    public static void main(String[] args) {
        UDPServer2 server = new UDPServer2();
        server.createAndListenSocket();
    }

}
