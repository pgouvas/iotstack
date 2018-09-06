package eu.orchestrator.transfer.entities.iotstack;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Topology implements Serializable{
    private List<Peer> peers;
    private List<Nodestat> nodes;
    
    public Topology() {
    }

    public Topology(List<Peer> peers, List<Nodestat> nodes) {
        this.peers = peers;
        this.nodes = nodes;
    }    
    
    public List<Peer> getPeers() {
        return peers;
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }    

    public List<Nodestat> getNodes() {
        return nodes;
    }

    public void setNodes(List<Nodestat> nodes) {
        this.nodes = nodes;
    }       
    
}//EoC
