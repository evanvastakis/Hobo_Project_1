import java.net.InetAddress;
import java.net.UnknownHostException;

public class Protocol {
    
    private double timeStamp; // Time at which packet was made.
    private int port; // Sender port 
    private String ip; // Sender ip
    private int destPort; // Destination port 
    private String destIp; // Destination ip
    private String name; // Name of node with its ip
    private String fileList; // List of that node's files

    public Protocol(){
        // Get ip
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            this.ip = "Unknown IP";
        }
    }

    // Getters

    public double getTimeStamp() {
        return timeStamp;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public int getDestPort() {
        return destPort;
    }

    public String getDestIp() {
        return destIp;
    }

    public String getName() {
        return name;
    }

    public String getFileList() {
        return fileList;
    }

    // Setters

    public void setTimeStamp(double timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList;
    }
}
