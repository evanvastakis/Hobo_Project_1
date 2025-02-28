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
 *  
 * 
 */
public class UDPServer2
{
    final int NODE_COUNT = 6;
    private boolean[] nodeStatus = new boolean[NODE_COUNT];
    private HashMap<String, InetAddress> nodeIPS = new HashMap<String, InetAddress>();
    private String[] IPKeys = {"node1", "node2", "node3", "node4", "node5", "node6"};
    // private int[] nodeTimes = new int[NODE_COUNT];

    private DatagramSocket socket = null;

    public UDPServer2() 
    {
    	try 
    	{
    		//create the socket assuming the server is listening on port 9876
			socket = new DatagramSocket(9876);
		} 
    	catch (SocketException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    public void createAndListenSocket() 
    {
        try 
        {
        	//incoming data buffer
            byte[] incomingData = new byte[1024];
            for(String i : IPKeys){
                nodeIPS.put(i, InetAddress.getLocalHost());
            }

            while (true) 
            {
            	//create incoming packet
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                System.out.println("Waiting...");
                
                //wait for the packet to arrive and store it in incoming packet
                socket.receive(incomingPacket);

                // this is meant to fill up the hash map with different IP values
                for (int i = 0; i < NODE_COUNT; i++) {
                    InetAddress currentAddress = nodeIPS.get(IPKeys[i]);
                
                    // Only replace the slot if it matches the local IP and the incoming IP is not already in the map
                    if (currentAddress.equals(InetAddress.getLocalHost()) && !nodeIPS.containsValue(incomingPacket.getAddress())) {
                        nodeIPS.put(IPKeys[i], incomingPacket.getAddress());
                        break; // Exit loop after inserting the new address
                    }
                }
            
                for(int i = 0; i < NODE_COUNT; i++){
                    System.out.println(nodeIPS.get(IPKeys[i]));
                }

                // this will be how we will make sure that the node is up or down
                for(int i = 0; i < NODE_COUNT; i++){
                    // match incoming packet to its status 
                    if(nodeIPS.get(IPKeys[i]).equals(incomingPacket.getAddress())){
                        nodeStatus[i] = true;
                    }
                    System.out.println("Node " + (i+1) + " is alive: " + nodeStatus[i]);
                }
                //retrieve the data
                String message = new String(incomingPacket.getData());
                
                //terminate if it is "THEEND" message from the client
                if(message.equals("THEEND"))
                {
                	socket.close();
                	break;
                }
                System.out.println("Received message from client: " + message);
                System.out.println("Client Details:PORT " + incomingPacket.getPort()
                + ", IP Address:" + incomingPacket.getAddress());
                
                //retrieve client socket info and create response packet
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                String reply = "Thank you for the message";
                byte[] data = reply.getBytes();
                DatagramPacket replyPacket =
                        new DatagramPacket(data, data.length, IPAddress, port);
                socket.send(replyPacket);
            }
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException i) 
        {
            i.printStackTrace();
        } 
    }

    public static void main(String[] args) 
    {
        UDPServer2 server = new UDPServer2();
        server.createAndListenSocket();
    }
}
