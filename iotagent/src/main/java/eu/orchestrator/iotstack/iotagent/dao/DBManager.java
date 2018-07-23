package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.transfer.Node;
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
    public void updatePeers(List<Peer> addlist, List<Peer> dellist)  {
        logger.info("updatePeers additions: "+addlist.size() +" deletions: "+dellist.size());
        for (Peer peer : dellist) {
            peerrepo.deleteById(peer.getFromnode(), peer.getTonode());
        }
        for (Peer peer : addlist) {
            peerrepo.insert(peer);
        }        
    }//EoM
    
    @Transactional
    public void updateGateway(String gatewayid){
        Node node = noderepo.findById(IoTAgent.nodeid);
        node.setGateway(gatewayid);
        noderepo.update(node);
    }//EoM
    
}//EoC
