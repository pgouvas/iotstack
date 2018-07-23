package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.transfer.CommandBroadcastUpdateGateway;
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
    @Autowired
    AsyncExecutors async;

    @Transactional    //(rollbackFor = AgentException.class)
    public void updatePeers(List<Peer> addlist, List<Peer> dellist) {
        logger.info("DBManager.updatePeers additions: " + addlist.size() + " deletions: " + dellist.size());
        for (Peer peer : dellist) {
            peerrepo.deleteById(peer.getFromnode(), peer.getTonode());
        }
        for (Peer peer : addlist) {
            peerrepo.insert(peer);
        }
    }//EoM

    @Transactional
    public void updateGateway(CommandBroadcastUpdateGateway cug) {
        //TODO itnegrate commandlog repo
        logger.info("DBManager.updateGateway");
        Node node = noderepo.findById(IoTAgent.nodeid);
        node.setGateway(cug.getGatewayid());
        noderepo.update(node);
    }//EoM

    @Transactional
    public void forwardupdateGatewayCommand(CommandBroadcastUpdateGateway cug) {
        List<Node> adjacentnodes = peerrepo.getAdjacentNodes(IoTAgent.nodeid);
        //avoid loops by removing solicitor and gateway
        adjacentnodes.remove(new Node(cug.getGatewayid()));
        adjacentnodes.remove(new Node(cug.getSolicitorid()));
        logger.info("DBManager.updateGateway will be sent to " + adjacentnodes.size());
        for (Node adjacentnode : adjacentnodes) {
            async.notifyAdjacentNodesForGateway(cug, adjacentnode.getId());
        }//for
    }//EoM

}//EoC
