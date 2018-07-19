package eu.orchestrator.iotstack.iotagent.scheduled;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.iotstack.transfer.Node;
import eu.orchestrator.iotstack.transfer.Peer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Panagiotis Gouvas
 */
@Component
public class ScheduledTasks {

    private static final Logger logger = Logger.getLogger(ScheduledTasks.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    NodeRepository noderepo;

//    @Scheduled(fixedRate = 5000)
//    public void reportCurrentTime() {
//        logger.info("The time is now {}" + dateFormat.format(new Date()) + IoTAgent.activeProfile);
//    }//EoM
    @Scheduled(fixedRate = 10000)
    public void scanLayer2Neighborhood() {
        Node node = Util.getNodeStatus();
        node.setLastupdate(new Date());
        noderepo.insert(node);
        
//        List<Peer> peers = node.getPeers();
//        for (Peer peer : peers) {
//        }//for

    }//EoM

    @Scheduled(fixedRate = 5000)
    public void broadcastClusterHead() {
        if (IoTAgent.isClusterHead()) {
            logger.info("Broadcasting clusterhead info");
            Node node = Util.getNodeStatus();
            List<Peer> peers = node.getPeers();
            for (Peer peer : peers) {
                String invokeresult = Util.invokeRest(peer.getTo());
                logger.info("Result " + invokeresult);
            }//for
        } else { //simple node   
            //do nothing
        }
    }//EoM

}//EoC
