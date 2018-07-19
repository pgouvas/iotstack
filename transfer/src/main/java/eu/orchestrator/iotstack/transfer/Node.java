package eu.orchestrator.iotstack.transfer;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Node {

    private String id;
    private List<Peer> peers;
    private Date lastupdate;
    
    public Node() {
    }    
    
    public Node(String id) {
        this.id = id;
    }

    public Node(String id, List<Peer> peers) {
        this.id = id;
        this.peers = peers;
    }    

    public Node(String id, List<Peer> peers, Date lastupdate) {
        this.id = id;
        this.peers = peers;
        this.lastupdate = lastupdate;
    }    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Peer> getPeers() {
        return peers;
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }

    public Date getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }    
    
    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", peers=" + peers + '}';
    }    
    
}
