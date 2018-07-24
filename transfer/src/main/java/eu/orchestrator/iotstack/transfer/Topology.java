package eu.orchestrator.iotstack.transfer;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Topology implements Serializable{
    private List<Peer> peers;

    public Topology() {
    }

    public Topology(List<Peer> peers) {
        this.peers = peers;
    }    
    
    public List<Peer> getPeers() {
        return peers;
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }    
    
}//EoC
