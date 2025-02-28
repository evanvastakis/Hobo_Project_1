import java.net.InetAddress;
import java.net.UnknownHostException;

public class Protocol {
    
    private double timeStamp; // Time at which packet was made.
    private int portNum; // Sender port num
    private String ip; // Sender ip
    private int destPortNum; // Destination port num
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

    public int getPortNum() {
        return portNum;
    }

    public String getIp() {
        return ip;
    }

    public int getDestPortNum() {
        return destPortNum;
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

    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setDestPortNum(int destPortNum) {
        this.destPortNum = destPortNum;
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
