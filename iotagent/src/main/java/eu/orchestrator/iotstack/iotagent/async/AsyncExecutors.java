package eu.orchestrator.iotstack.iotagent.async;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
import eu.orchestrator.iotstack.transfer.CommandUpdatePeers;
import eu.orchestrator.iotstack.transfer.Node;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
            
    @Async
    public void notifyGatewayForPeerChanges(CommandUpdatePeers cup) {
        Node node = noderepo.findById(IoTAgent.nodeid);
        String gateway = node.getGateway();
        logger.info("Notifying gateway "+gateway + "  for insertions: "+cup.getAddlist().size()+" deletions "+cup.getDellist().size());
        if ( (gateway!=null) && (!gateway.equalsIgnoreCase("")) ){
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://[" + gateway + "]:8080/api/v1/peers";
            try {
                logger.info("Performing rest");
                restTemplate.postForObject(url, cup ,String.class);
                logger.info("Response at " + gateway);
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
            }            
        }
    }//EoM
    
    
    //TODO build notifier
    //TODO implement forwardedwithout broadcasting
    //TODO implement reporterid to peer
    //TODO implement gateway keepalive listener
    @Async      
    public void notifyNodeForGateway(CommandUpdatePeers cup) {
        Node node = noderepo.findById(IoTAgent.nodeid);
        String gateway = node.getGateway();
        logger.info("Notifying gateway "+gateway + "  for insertions: "+cup.getAddlist().size()+" deletions "+cup.getDellist().size());
        if ( (gateway!=null) && (!gateway.equalsIgnoreCase("")) ){
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://[" + gateway + "]:8080/api/v1/peers";
            try {
                logger.info("Performing rest");
                restTemplate.postForObject(url, cup ,String.class);
                logger.info("Response at " + gateway);
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
            }            
        }
    }//EoM    
    
    
}//EoC
