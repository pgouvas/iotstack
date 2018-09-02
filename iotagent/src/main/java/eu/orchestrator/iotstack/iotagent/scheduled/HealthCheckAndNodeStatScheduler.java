package eu.orchestrator.iotstack.iotagent.scheduled;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.iotagent.dao.NodeRepository;
import eu.orchestrator.iotstack.iotagent.dao.PeerRepository;
import eu.orchestrator.transfer.entities.iotstack.Node;
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
public class HealthCheckAndNodeStatScheduler {

    private static final Logger logger = Logger.getLogger(HealthCheckAndNodeStatScheduler.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    PeerRepository peerrepo;
    @Autowired
    NodeRepository noderepo;
    @Autowired
    AsyncExecutors async;

    @Scheduled(fixedRate = 10000)
    public void getNodeStats() {
        if (IoTAgent.isGateway()) {
            //If only the gateway is active then the following call will include it
            List<Node> allnodes = peerrepo.getAllActiveAnnouncedNodes();
            Node mynode = noderepo.findById(IoTAgent.nodeid).get(0);
            if (!allnodes.contains(mynode)) allnodes.add(mynode);
            logger.info("Scheduled getNodeStats for " + allnodes.size() + " nodes " + dateFormat.format(new Date()));
            for (Node node : allnodes) {
                async.getNodeStats(node);
            }//for
        }
    }//EoM    

}//EoC
