package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.transfer.entities.iotstack.CommandBroadcastUpdateGateway;
import eu.orchestrator.transfer.entities.iotstack.CommandUnicastUpdatePeers;
import eu.orchestrator.transfer.entities.iotstack.Credentials;
import eu.orchestrator.transfer.entities.iotstack.Node;
import eu.orchestrator.transfer.entities.iotstack.Nodestat;
import eu.orchestrator.transfer.entities.iotstack.Peer;
import eu.orchestrator.transfer.entities.iotstack.ResourceModel;
import eu.orchestrator.transfer.entities.iotstack.ResponseCode;
import eu.orchestrator.transfer.entities.iotstack.RestResponse;
import eu.orchestrator.transfer.entities.iotstack.Topology;
import java.util.ArrayList;
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
    
//    @Transactional
    public void updatePeersLocal(List<Peer> addlist, List<Peer> dellist) {
        logger.info("DBManager.updatePeers additions: " + addlist.size() + " deletions: " + dellist.size());
        for (Peer peer : dellist) {
            peerrepo.deleteById(peer.getFromnode(), peer.getTonode());
        }

        List<Peer> toremove = new ArrayList<>();
        //check douples
        List<Peer> existinglist = peerrepo.findAll();
        for (Peer addpeer : addlist) {
            if (existinglist.contains(addpeer)) {
                toremove.add(addpeer);
                //addlist.remove(addpeer);
            }
        }//for         

        //actual removing
        for (Peer peer : toremove) {
            addlist.remove(peer);
        }

        for (Peer peer : addlist) {
            peerrepo.insert(peer);
        }
    }//EoM

    //--------------------Commands Handling-----------------    
//    @Transactional
    public void updatePeersRemote(CommandUnicastUpdatePeers updatecommand) {
        List<Peer> addlist = updatecommand.getAddlist();
        List<Peer> dellist = updatecommand.getDellist();
        logger.info("DBManager.updatePeers additions: " + addlist.size() + " deletions: " + dellist.size());
        for (Peer peer : dellist) {
            peerrepo.deleteById(peer.getFromnode(), peer.getTonode());
        }

        List<Peer> toremove = new ArrayList<>();
        //Prevent douples
        List<Peer> existinglist = peerrepo.findAll();
        for (Peer addpeer : addlist) {
            if (existinglist.contains(addpeer)) {
                toremove.add(addpeer);
                //addlist.remove(addpeer);
            }
        }//for

        //actual removing
        for (Peer peer : toremove) {
            addlist.remove(peer);
        }

        for (Peer peer : addlist) {
            peerrepo.insert(peer);
        }
    }//EoM    

//    @Transactional
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
                //TODO callables here
                Util.notifyAdjacentNodesForGateway(cbug, adjacentnode.getId());
            }//for
            clogrepo.insert(new Commandlog(cbug.getCid(), cbug.getCdate()));
        } else {
            //ignoring command
            logger.info("DBManager.updateGateway " + cbug.getCid() + " ignored!");
        }
    }//EoM

//    @Transactional
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

//    @Transactional
    public Nodestat getNodestatsForRemoteReporting() {
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

//    @Transactional
    public void updateNodestat(Nodestat nodestat) {
        logger.info("DBManager updating nodestats for " + nodestat.getNodeid());
        if (nodestatrepo.findById(nodestat.getNodeid()).isEmpty()) {
            nodestatrepo.insert(nodestat);
        } else {
            nodestatrepo.updateRemote(nodestat);
        }
    }//EoM

//    @Transactional
    public void updateNodestatForBandwith(String nodeid, String bandwidth) {
//        logger.info("DBManager updating nodestats for bandwidth" + nodeid);
        nodestatrepo.updateLocalBandwidth(bandwidth, nodeid);
    }//EoM    

//    @Transactional
    public void updateNodestatForRTTDelay(String nodeid, String rttdelay) {
//        logger.info("DBManager updating nodestats for rttdelay" + nodeid);
        nodestatrepo.updateLocalRttdelay(rttdelay, nodeid);
    }//EoM

//    @Transactional
    public void updateNodestatForPacketLoss(String nodeid, String packetloss) {
//        logger.info("DBManager updating nodestats for packetloss" + nodeid);
        nodestatrepo.updateLocalPacketloss(packetloss, nodeid);
    }//EoM    

//    @Transactional
    public ResourceModel getResources() {
        logger.info("getResources vcpus: " + nodestatrepo.getMaxVCPUs() + " ram: " + nodestatrepo.getMaxRam());
        ResourceModel resourcemodel = new ResourceModel();
        resourcemodel.setId(IoTAgent.nodeid);
        
        int maxram = nodestatrepo.getMaxRam();
        resourcemodel.setMaxRam(maxram);
        
        int maxvcpus = nodestatrepo.getMaxVCPUs();
        resourcemodel.setMaxVCpus(maxvcpus);
        
        int maxinstances = nodestatrepo.getMaxInstances();
        resourcemodel.setMaxInstances( maxinstances );
        
        int runninginstances = nodestatrepo.getRunningInstances();
        resourcemodel.setRunningInstances(runninginstances);
        
        int usedram = nodestatrepo.getUsedRam();
        resourcemodel.setUsedRam(usedram);
        
        int usedvcpus = nodestatrepo.getUsedVCpus();
        resourcemodel.setUsedVCpus(usedvcpus);
        
        resourcemodel.setvCpuUtilization(  ((double)usedvcpus) / ((double)maxvcpus) );
        
        resourcemodel.setRamUtilization( ((double)usedram) / ((double)maxram) );
        
        resourcemodel.setInstancesUtilization( ((double)runninginstances) / ((double)maxinstances) );
        
        return resourcemodel;
    }//EoM

//    @Transactional
    public Topology getTopology() {
        Topology topology = new Topology();
        topology.setPeers(peerrepo.findAllActive());
        topology.setNodes(nodestatrepo.findAll());
        return topology;
    }//EoM

}//EoC
