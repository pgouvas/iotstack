package eu.orchestrator.iotstack.transfer;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Peer {

    private String from;
    private String to;
    private String timestamp;

    public Peer(String from, String to, String timestamp) {
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
    }    
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
