package eu.orchestrator.iotstack.iotagent.scheduled;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.iotstack.transfer.Node;
import eu.orchestrator.iotstack.transfer.Peer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
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

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        logger.info("The time is now {}" + dateFormat.format(new Date()) + IoTAgent.activeProfile);
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