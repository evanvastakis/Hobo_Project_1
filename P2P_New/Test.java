import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.concurrent.*;

public class Test extends P2P_Protocol implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int serverPort = 9876;
    private static final int TIMEOUT_MS = 5000;
    private static final String[] IPS = {"10.111.134.82", "10.111.142.78", "10.111.152.150", "", "", ""};

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

            for (String ip : IPS) {
                if (ip.isEmpty()) continue;
                InetAddress destIp = InetAddress.getByName(ip);
                scheduler.scheduleAtFixedRate(() -> {
                    try {
                        sendFiles(destIp);
                        receiveFiles();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 0, secureRandom.nextInt(30) + 1, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFiles(InetAddress destIp) throws IOException {
        File folder = new File("C:\\Users\\evanv\\OneDrive\\Computer_Science\\SophomoreYear\\CSC340\\Hobo_Project_1\\P2P_New");
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
        System.out.println("Sent files to " + destIp.getHostAddress());
    }

    private void receiveFiles() {
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received: " + response);
        } catch (SocketTimeoutException e) {
            System.out.println("No response received. Node might be offline.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Test client = new Test();
        client.communicate();
    }
}