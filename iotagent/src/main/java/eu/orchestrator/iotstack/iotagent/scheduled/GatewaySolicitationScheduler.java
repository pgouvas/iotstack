package eu.orchestrator.iotstack.iotagent.scheduled;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Panagiotis Gouvas
 */
@Component
public class GatewaySolicitationScheduler {
    //Rule of thump: All business logic of schedulers should be implemented as Async. A blocking method blocks the entire thread
    private static final Logger logger = Logger.getLogger(GatewaySolicitationScheduler.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

//    @Scheduled(fixedRate = 5000)
//    public void broadcastClusterHead() {
//        if (IoTAgent.isClusterHead()) {
//            logger.info("Broadcasting clusterhead info");
//            Node node = Util.getNodeStatus();
//            List<Peer> peers = node.getPeers();
//            for (Peer peer : peers) {
//                String invokeresult = Util.invokeRest(peer.getTo());
//                logger.info("Result " + invokeresult);
//            }//for
//        } else { //simple node   
//            //do nothing
//        }
//    }//EoM
    
    
    
}//EoC
