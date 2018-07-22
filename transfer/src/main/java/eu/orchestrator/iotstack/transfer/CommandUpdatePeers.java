package eu.orchestrator.iotstack.transfer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Panagiotis Gouvas
 */
public class CommandUpdatePeers {

    List<Peer> addlist;
    List<Peer> dellist;

    public CommandUpdatePeers() {
    }    
    
    public CommandUpdatePeers(List<Peer> addlist, List<Peer> dellist) {
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
