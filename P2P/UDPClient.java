import java.io.IOException;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
/**
 * 
 * @author cjaiswal
 *
 *  
 * 
 */
public class UDPClient 
{
    static SecureRandom secureRandom = new SecureRandom();
    // int random = secureRandom.nextInt(30) + 1;
    DatagramSocket Socket;

    public UDPClient() 
    {

    }

    public void createAndListenSocket() 
    {
        try 
        { 
            // random = secureRandom.nextInt(30) + 1;
            Socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("10.111.142.78");
            byte[] incomingData = new byte[1024];
            String sentence = "HI EVAN FROM GRANT";
            byte[] data = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            Socket.send(sendPacket);
            System.out.println("Message sent from client");
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            Socket.receive(incomingPacket);
            String response = new String(incomingPacket.getData());
            System.out.println("Response from server:" + response);
            Socket.close();
        }
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException 
    {
        UDPClient client = new UDPClient();
        while(true){
            int random = secureRandom.nextInt(5) + 1;
            System.out.println("Waiting for " + random + " seconds");
            TimeUnit.SECONDS.sleep(random);
            System.out.println("We are waiting");   
            client.createAndListenSocket();
        }
    }
}

