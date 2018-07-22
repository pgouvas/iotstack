package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.transfer.Peer;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Panagiotis Gouvas
 */
@Component
public class DBManager {

    private static final Logger logger = Logger.getLogger(DBManager.class.getName());    
    
    @Autowired
    NodeRepository noderepo;    
    @Autowired
    PeerRepository peerrepo; 
    
    @Transactional    //(rollbackFor = AgentException.class)
    public void updateNeighbor(List<Peer> addlist, List<Peer> dellist)  {
        logger.info("updateNeighbor additions: "+addlist.size() +" deletions: "+dellist.size());
        for (Peer peer : dellist) {
            peerrepo.deleteById(peer.getFromnode(), peer.getTonode());
        }
        for (Peer peer : addlist) {
            peerrepo.insert(peer);
        }        
    }//EoM
    
}
