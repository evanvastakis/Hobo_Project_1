import java.net.InetAddress;

public class C2S_Protocol {
    
    private double timeStamp; // Time at which packet was made.
    private int port; // Sender port 
    private InetAddress ip; // Sender ip
    private int destPort; // Destination port 
    private InetAddress destIp; // Destination ip
    private String name; // Name of node with its ip
    private String fileList; // List of that node's files

    public C2S_Protocol(){
    }

    // Getters

    public double getTimeStamp() {
        return timeStamp;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getDestPort() {
        return destPort;
    }

    public InetAddress getDestIp() {
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

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public void setDestIp(InetAddress destIp) {
        this.destIp = destIp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList;
    }
}
