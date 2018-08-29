package eu.orchestrator.iotstack.iotagent.async;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.dao.DBManager;
import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
import eu.orchestrator.iotstack.transfer.CommandBroadcastUpdateGateway;
import eu.orchestrator.iotstack.transfer.CommandUnicastUpdatePeers;
import eu.orchestrator.iotstack.transfer.Node;
import eu.orchestrator.iotstack.transfer.Nodestat;
import eu.orchestrator.iotstack.transfer.ResponseCode;
import eu.orchestrator.iotstack.transfer.RestResponse;
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
    NodeRepository noderepo;
    @Autowired
    DBManager dbmanager;

    @Async
    public void notifyGatewayForPeerChanges(CommandUnicastUpdatePeers cup) {
        Node node = noderepo.findById(IoTAgent.nodeid).get(0);
        String gateway = node.getGateway();
        logger.info("Notifying gateway " + gateway + "  for insertions: " + cup.getAddlist().size() + " deletions " + cup.getDellist().size());
        if ((gateway != null) && (!gateway.equalsIgnoreCase(""))) {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://[" + gateway + "]:8080/api/v1/peers";
            try {
                //logger.info("Performing rest");
                restTemplate.postForObject(url, cup, String.class);
                //logger.info("Response at " + gateway);
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
            }
        }
    }//EoM

    @Async
    public void notifyAdjacentNodesForGateway(CommandBroadcastUpdateGateway cug, String targetid) {
        logger.info("Notifying notifyAdjacentNodesForGateway " + targetid + "  for gateway: " + cug.getGatewayid());
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + targetid + "]:8080/api/v1/gateway";
        try {
            //logger.info("Performing rest");
            restTemplate.postForObject(url, cug, String.class);
            //logger.info("Response at " + targetid);
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
        }
    }//EoM

    @Async
    public void getNodeStats(Node node) {
        logger.info("getNodeStats for " + node.getId());
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + node.getId() + "]:8080/api/v1/nodestats";
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
            logger.severe("Communication Exception. Node not reachable or Agent not running for "+node.getId());
        }
    }//EoM
    
    
}//EoC
