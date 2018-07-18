package eu.orchestrator.iotstack.transfer;

import java.util.List;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Node {

    private String id;
    private List<Peer> peers;

    public Node() {
    }    
    
    public Node(String id) {
        this.id = id;
    }

    public Node(String id, List<Peer> peers) {
        this.id = id;
        this.peers = peers;
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

    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", peers=" + peers + '}';
    }    
    
}
