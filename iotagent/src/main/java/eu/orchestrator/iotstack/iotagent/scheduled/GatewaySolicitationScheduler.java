package eu.orchestrator.iotstack.iotagent.scheduled;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.iotagent.dao.Commandlog;
import eu.orchestrator.iotstack.iotagent.dao.CommandlogRepository;
import eu.orchestrator.iotstack.iotagent.dao.PeerRepository;
import eu.orchestrator.transfer.entities.iotstack.CommandBroadcastUpdateGateway;
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
public class GatewaySolicitationScheduler {

    //Rule of thump: All business logic of schedulers should be implemented as Async. A blocking method blocks the entire thread
    private static final Logger logger = Logger.getLogger(GatewaySolicitationScheduler.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    PeerRepository peerrepo;
    @Autowired
    CommandlogRepository clrepo;    
    @Autowired
    AsyncExecutors async;

    @Scheduled(fixedRate = 10000)
    public void broadcastGateway() {
        logger.info("Scheduled BroadcastGateway: " + dateFormat.format(new Date()));
        if (IoTAgent.isGateway()) {
            //Each creation of broadcast is accompanied by log entry
            CommandBroadcastUpdateGateway cug = new CommandBroadcastUpdateGateway(IoTAgent.nodeid,IoTAgent.nodeid,IoTAgent.nodeid);
            clrepo.insert(new Commandlog(cug.getCid(), new Date()));
            //get All nodes and not only reported Alive
            List<Node> adjacentnodes = peerrepo.getAdjacentNodes(IoTAgent.nodeid);
            for (Node adjacentnode : adjacentnodes) {
                async.notifyAdjacentNodesForGateway(cug,adjacentnode.getId());                
            }//for
        }//if
    }//EoM

}//EoC
