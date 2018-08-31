package eu.orchestrator.transfer.entities.iotstack;

import eu.orchestrator.transfer.entities.iotstack.Peer;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Panagiotis Gouvas
 */
public class CommandUnicastUpdatePeers implements Serializable{

    private List<Peer> addlist;
    private List<Peer> dellist;

    public CommandUnicastUpdatePeers() {
    }
    
    public CommandUnicastUpdatePeers(List<Peer> addlist, List<Peer> dellist) {
        this.addlist = addlist;
        this.dellist = dellist;
    }
    
    public List<Peer> getAddlist() {
        return addlist;
    }

    public void setAddlist(List<Peer> addlist) {
        this.addlist = addlist;
    }

    public List<Peer> getDellist() {
        return dellist;
    }

    public void setDellist(List<Peer> dellist) {
        this.dellist = dellist;
    }    
    
}//EoC
