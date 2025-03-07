import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.*;

public class Test extends P2P_Protocol implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int serverPort = 9876;
    private static final int TIMEOUT_MS = 5000;
    private static final String[] IPS = {"10.111.134.82", "10.111.142.78", "10.111.152.150", "33.333.333.33", "44.444.444.44", "55.555.555.55"};

    private transient DatagramSocket socket;
    private static final SecureRandom secureRandom = new SecureRandom();

    public Test() {
        try {
            socket = new DatagramSocket(serverPort);
            socket.setSoTimeout(TIMEOUT_MS);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void communicate() {
        try {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(IPS.length);
            InetAddress localAddress = InetAddress.getLocalHost();

            for (int i = 0; i < IPS.length; i++) {
                String ip = IPS[i];
                if (ip.isEmpty() || ip.equals(localAddress.getHostAddress())) continue;
                
                int nodeNumber = i + 1;  // Assign node number (1-based index)
                InetAddress destIp = InetAddress.getByName(ip);

                scheduler.scheduleAtFixedRate(() -> {
                    try {
                        sendFiles(destIp, nodeNumber);
                        receiveFiles(nodeNumber, destIp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 0, secureRandom.nextInt(30) + 1, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFiles(InetAddress destIp, int nodeNumber) throws IOException {
        File folder = new File("/Users/grant/Downloads/Course Materials/Spring 2025/CSC340/Hobo_Project_1/C2S_New");
        File[] files = folder.listFiles();
        StringBuilder fileList = new StringBuilder();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.append(file.getName()).append(", ");
                }
            }
        }

        byte[] data = fileList.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, destIp, serverPort);
        socket.send(packet);
        System.out.println("Sent files to Node " + nodeNumber + " (" + destIp.getHostAddress() + ")");
    }

    private void receiveFiles(int nodeNumber, InetAddress destIp) {
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            InetAddress senderAddress = packet.getAddress();
            String response = new String(packet.getData(), 0, packet.getLength());

            int senderNodeNumber = -1;
            for (int i = 0; i < IPS.length; i++) {
                if (IPS[i].equals(senderAddress.getHostAddress())) {
                    senderNodeNumber = i + 1;
                    break;
                }
            }

            if (senderNodeNumber != -1) {
                System.out.println("Received from Node " + senderNodeNumber + " (" + senderAddress.getHostAddress() + "): " + response);
            } else {
                System.out.println("Received from Unknown Node (" + senderAddress.getHostAddress() + "): " + response);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("No response from Node " + nodeNumber + " (" + destIp.getHostAddress() + "). Node might be offline.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Test client = new Test();
        client.communicate();
    }
}
