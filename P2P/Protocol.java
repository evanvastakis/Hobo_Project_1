public class Protocol {
    
    private double timeStamp; // Time at which packet was made.
    private int portNum; // Sender port num
    private int ip; // Sender ip
    private int destPortNum; // Destination port num
    private int destIp; // Destination ip
    private String name; // Name of node with its ip
    private byte[] fileList; // List of that node's files

    public Protocol(){
        
    }

}
