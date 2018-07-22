package eu.orchestrator.iotstack.iotagent.async;

import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
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
    public static String notifyGateway(        String ipv6ofgw ) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://[" + ipv6ofgw + "]:8080/api/v1/echo";
        String response = "";
        logger.info("Invoking service echo at " + ipv6ofgw);
        try {
            response = restTemplate.getForObject(url, String.class);
            logger.info("Response at " + ipv6ofgw);
        } catch (Exception ex) {
            logger.severe(url + " is unreachable");
        }
        return response;
    }//EoM
    
   
    
}
