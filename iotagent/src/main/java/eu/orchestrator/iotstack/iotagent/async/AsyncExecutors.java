package eu.orchestrator.iotstack.iotagent.async;

import eu.orchestrator.agent.Agent;
import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.dao.DBManager;
import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
import eu.orchestrator.iotstack.iotagent.dao.PeerRepository;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.transfer.entities.iotstack.CommandBroadcastUpdateGateway;
import eu.orchestrator.transfer.entities.iotstack.CommandUnicastUpdatePeers;
import eu.orchestrator.transfer.entities.iotstack.IoTBootRequest;
import eu.orchestrator.transfer.entities.iotstack.Node;
import eu.orchestrator.transfer.entities.iotstack.Nodestat;
import eu.orchestrator.transfer.entities.iotstack.ResponseCode;
import eu.orchestrator.transfer.entities.iotstack.RestResponse;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Panagiotis Gouvas
 */
@Service
public class AsyncExecutors {

    //All methods should be async
    private static final Logger logger = Logger.getLogger(AsyncExecutors.class.getName());

    @Autowired
    DBManager dbmanager;    
    @Autowired
    NodeRepository noderepo;
    @Autowired
    PeerRepository peerrepo;
    
    @Async
    public void notifyGatewayForPeerChanges(CommandUnicastUpdatePeers cup) {
        Node node = noderepo.findById(IoTAgent.nodeid).get(0);
        String gateway = node.getGateway();
        logger.info("Notifying gateway " + gateway + "  for insertions: " + cup.getAddlist().size() + " deletions " + cup.getDellist().size());
        if ((gateway != null) && (!gateway.equalsIgnoreCase(""))) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://[" + gateway + "]:8080/api/v1/peers";
            try {
                restTemplate.postForObject(url, cup, String.class);
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
            }
        }
    }//EoM

    @Async
    public void notifyAdjacentNodesForGateway(CommandBroadcastUpdateGateway cug, String targetid) {
        logger.info("notifyAdjacentNodesForGateway " + targetid + "  for gateway: " + cug.getGatewayid());
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + targetid + "]:8080/api/v1/gateway";
        try {
            restTemplate.postForObject(url, cug, String.class);
        } catch (Exception ex) {
            logger.info("Peer to "+targetid +" was considered active but notifyAdjacentNodesForGateway failed."); 
            peerrepo.updateStatus(IoTAgent.nodeid, targetid, false);
        }
    }//EoM

    @Async
    public void getNodeStats(Node targetnode) {
        logger.info("getNodeStats for " + targetnode.getId());
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + targetnode.getId() + "]:8080/api/v1/nodestats";
        try {
            //logger.info("Performing rest");
            ParameterizedTypeReference<RestResponse<Nodestat>> typeref = new ParameterizedTypeReference<RestResponse<Nodestat>>() {
            };
            ResponseEntity<RestResponse<Nodestat>> resp = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, typeref);
            RestResponse<Nodestat> response = resp.getBody();
            if (response.getRescode() == ResponseCode.SUCCESS) {
                Nodestat nodestat = (Nodestat) (response.getResobject());
                dbmanager.updateNodestat(nodestat);
            }//if
        } catch (Exception ex) {
            logger.info("Peer to "+targetnode.getId() +" was considered active but notifyAdjacentNodesForGateway failed."); 
            peerrepo.updateStatus(IoTAgent.nodeid, targetnode.getId(), false);
        }
    }//EoM

    @Async
    public void measureBandwidth(Node node) {
        logger.info("MeasureBandwidth for " + node.getId());
        try {
            String output = Util.measureBandwith(node.getId());
            dbmanager.updateNodestatForBandwith(node.getId(), output);
            logger.info("Bandwith for " + node.getId() + " : " + output);
        } catch (Exception ex) {
        }
    }//EoM        

    @Async
    public void measureRTTDelay(Node node) {
        logger.info("MeasureRTTDelay for " + node.getId());
        try {
            String output = Util.measureRTTDelay(node.getId());
            dbmanager.updateNodestatForRTTDelay(node.getId(), output);
            logger.info("RTTDelay for " + node.getId() + " : " + output);
        } catch (Exception ex) {
        }
    }//EoM  

    @Async
    public void measurePacketloss(Node node) {
        logger.info("MeasurePacketloss for " + node.getId());
        try {
            String output = Util.measurePacketLoss(node.getId());
            dbmanager.updateNodestatForPacketLoss(node.getId(), output);
            logger.info("Packetloss for " + node.getId() + " : " + output);
        } catch (Exception ex) {
        }
    }//EoM           

    @Async
    public void bootAgent(IoTBootRequest request) {
        Agent agent = new Agent();
        String[] args = new String[7];
        args[0] = request.getGraphID();
        args[1] = request.getGraphInstanceID();
        args[2] = request.getComponentNodeID();
        args[3] = request.getComponentNodeInstanceID();
        args[4] = (""+request.getFlag()).toLowerCase();
        args[5] = (""+request.getLb()).toLowerCase();
        args[6] = (""+request.getIpv6()).toLowerCase();
        logger.info("Booting Agent");
        agent.bootAgent(args);
    }//EoM
    
}//EoC
