package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.transfer.CommandBroadcastUpdateGateway;
import eu.orchestrator.iotstack.transfer.CommandUnicastUpdatePeers;
import eu.orchestrator.iotstack.transfer.Credentials;
import eu.orchestrator.iotstack.transfer.Node;
import eu.orchestrator.iotstack.transfer.Nodestat;
import eu.orchestrator.iotstack.transfer.Peer;
import eu.orchestrator.iotstack.transfer.ResourceModel;
import eu.orchestrator.iotstack.transfer.ResponseCode;
import eu.orchestrator.iotstack.transfer.RestResponse;
import eu.orchestrator.iotstack.transfer.Topology;
import java.util.Date;
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
    NodestatRepository nodestatrepo;
    @Autowired
    AsyncExecutors async;

    @Transactional
    public void updatePeersLocal(List<Peer> addlist, List<Peer> dellist) {
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
    public void updatePeersRemote(CommandUnicastUpdatePeers updatecommand) {
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
        logger.info("DBManager.updateGateway " + cbug.getCid());
        if (clogrepo.findById(cbug.getCid()).size() == 0) {
            //TODO itnegrate commandlog repo
            Node node = noderepo.findById(IoTAgent.nodeid).get(0);
            node.setGateway(cbug.getGatewayid());
            noderepo.update(node);
            //forwarding logic
            List<Node> adjacentnodes = peerrepo.getAdjacentActiveNodes(IoTAgent.nodeid);
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
            logger.info("DBManager.updateGateway " + cbug.getCid() + " ignored!");
        }
    }//EoM

    @Transactional
    public RestResponse validatecredentials(Credentials credentials) {
        RestResponse result = new RestResponse();
        if (credentials.getUsername().equalsIgnoreCase("admin") && credentials.getPassword().equalsIgnoreCase("!admin!")) {
            result.setRescode(ResponseCode.SUCCESS);
            result.setMessage("success");
        } else {
            result.setRescode(ResponseCode.INVALID);
            result.setMessage("Authorization failure");
        }
        return result;
    }//EoM

    @Transactional
    public Nodestat getNodestats() {
        Node node = noderepo.findById(IoTAgent.nodeid).get(0);
        Nodestat nodestat = new Nodestat();
        nodestat.setNodeid(node.getId());
        nodestat.setBootdate(node.getBootdate());
        nodestat.setCheckdate(new Date());
        nodestat.setCpuspeed(node.getCpuspeed());
        nodestat.setGateway(node.getGateway());
        nodestat.setOsarch(node.getOsarch());
        nodestat.setOsname(node.getOsname());
        nodestat.setTotalmemory(node.getTotalmemory());
        nodestat.setVcpus(node.getVcpus());
        return nodestat;
    }//EoM

    @Transactional
    public void updateNodestat(Nodestat nodestat) {
        logger.info("DBManager updating nodestats for " + nodestat.getNodeid());
        if (nodestatrepo.findById(nodestat.getNodeid()).isEmpty()) {
            nodestatrepo.insert(nodestat);
        } else {
            nodestatrepo.update(nodestat);
        }
    }//EoM

    @Transactional
    public ResourceModel getResources() {
        logger.info("getResources vcpus: " + nodestatrepo.getMaxVCPUs() +" ram: "+nodestatrepo.getMaxRam());
        ResourceModel resourcemodel = new ResourceModel();
        resourcemodel.setId(IoTAgent.nodeid);
        resourcemodel.setMaxRam(nodestatrepo.getMaxRam());
        resourcemodel.setMaxVCpus(nodestatrepo.getMaxVCPUs());
        resourcemodel.setMaxInstances(0);
        resourcemodel.setRunningInstances(0);
        resourcemodel.setUsedRam(0);
        resourcemodel.setUsedVCpus(0);
        resourcemodel.setvCpuUtilization(0.0);
        resourcemodel.setRamUtilization(0.0);
        resourcemodel.setInstancesUtilization(0.0);
        return resourcemodel;
    }//EoM

    @Transactional    
    public Topology getTopology() {
        Topology topology = new Topology();
        topology.setPeers( peerrepo.findAll() );
        return topology;
    }//EoM

}//EoC
