package eu.orchestrator.iotstack.iotagent.scheduled;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.iotagent.dao.PeerRepository;
import eu.orchestrator.iotstack.transfer.Node;
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
public class PerformanceStatisticsScheduler {

    private static final Logger logger = Logger.getLogger(PerformanceStatisticsScheduler.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    PeerRepository peerrepo;
    @Autowired
    AsyncExecutors async;

    @Scheduled(fixedRate = 60000)
    public void getNodePerformanceStats() {
        if (IoTAgent.isGateway()) {
            logger.info("getNodePerformanceStats: " + dateFormat.format(new Date()));
            List<Node> allnodes = peerrepo.getAllActiveAnnouncedNodes();            
            for (Node node : allnodes) {
                async.measureBandwidth(node);
                async.measureRTTDelay(node);
                async.measurePacketloss(node);
            }//for
            
        }//if
    }//EoM    

}//EoC
