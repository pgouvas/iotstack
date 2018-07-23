package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.transfer.CommandBroadcastUpdateGateway;
import eu.orchestrator.iotstack.transfer.CommandUnicastUpdatePeers;
import eu.orchestrator.iotstack.transfer.Credentials;
import eu.orchestrator.iotstack.transfer.Node;
import eu.orchestrator.iotstack.transfer.Peer;
import eu.orchestrator.iotstack.transfer.ResponseCode;
import eu.orchestrator.iotstack.transfer.RestResponse;
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
    CommandlogRepository clogrepo;
    @Autowired
    NodeRepository noderepo;
    @Autowired
    PeerRepository peerrepo;
    @Autowired
    AsyncExecutors async;

    @Transactional
    public void updatePeers(List<Peer> addlist, List<Peer> dellist) {
        logger.info("DBManager.updatePeers additions: " + addlist.size() + " deletions: " + dellist.size());
        for (Peer peer : dellist) {
            peerrepo.deleteById(peer.getFromnode(), peer.getTonode());
        }
        //check douples
        List<Peer> existinglist = peerrepo.findAll();
        for (Peer addpeer : addlist) {
            if (existinglist.contains(addpeer)) {
                addlist.remove(addpeer);
            }
        }//for         

        for (Peer peer : addlist) {
            peerrepo.insert(peer);
        }
    }//EoM

    //--------------------Commands Handling    
    @Transactional
    public void updatePeers(CommandUnicastUpdatePeers updatecommand) {
        List<Peer> addlist = updatecommand.getAddlist();
        List<Peer> dellist = updatecommand.getDellist();
        logger.info("DBManager.updatePeers additions: " + addlist.size() + " deletions: " + dellist.size());
        for (Peer peer : dellist) {
            peerrepo.deleteById(peer.getFromnode(), peer.getTonode());
        }
        //Prevent douples
        List<Peer> existinglist = peerrepo.findAll();
        for (Peer addpeer : addlist) {
            if (existinglist.contains(addpeer)) {
                addlist.remove(addpeer);
            }
        }//for         

        for (Peer peer : addlist) {
            peerrepo.insert(peer);
        }
    }//EoM    

    @Transactional
    public void updateGateway(CommandBroadcastUpdateGateway cbug) {
        logger.info("DBManager.updateGateway "+cbug.getCid());
        if (clogrepo.findById(cbug.getCid()).size()==0) {
            //TODO itnegrate commandlog repo
            Node node = noderepo.findById(IoTAgent.nodeid);
            node.setGateway(cbug.getGatewayid());
            noderepo.update(node);
            //forwarding logic
            List<Node> adjacentnodes = peerrepo.getAdjacentNodes(IoTAgent.nodeid);
            //avoid loops by removing solicitor and gateway
            adjacentnodes.remove(new Node(cbug.getGatewayid()));
            adjacentnodes.remove(new Node(cbug.getSolicitorid()));
            logger.info("DBManager.updateGateway will be sent to " + adjacentnodes.size());
            cbug.setSolicitorid(IoTAgent.nodeid);
            for (Node adjacentnode : adjacentnodes) {
                async.notifyAdjacentNodesForGateway(cbug, adjacentnode.getId());
            }//for
            clogrepo.insert(new Commandlog(cbug.getCid(), cbug.getCdate()));
        } else {
            //ignoring command
            logger.info("DBManager.updateGateway "+cbug.getCid()+ " ignored!");
        }
    }//EoM
    
    @Transactional
    public RestResponse validatecredentials(Credentials credentials){
        RestResponse result = new RestResponse();
        if (credentials.getUsername().equalsIgnoreCase("admin") && credentials.getPassword().equalsIgnoreCase("!admin!")){
            result.setRescode(ResponseCode.SUCCESS);
            result.setMessage("success");            
        } else {            
            result.setRescode(ResponseCode.INVALID);
            result.setMessage("Authorization failure");
        }
        return result;
    }//EoM
    
}//EoC
