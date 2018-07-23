package eu.orchestrator.iotstack.iotagent.scheduled;

import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.iotagent.dao.PeerRepository;
import eu.orchestrator.iotstack.iotagent.dao.DBManager;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.iotstack.transfer.CommandUnicastUpdatePeers;
import eu.orchestrator.iotstack.transfer.Peer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class PeerUpdateScheduler {

    //Rule of thump: All business logic of schedulers should be implemented as Async. A blocking method blocks the entire thread
    private static final Logger logger = Logger.getLogger(PeerUpdateScheduler.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    PeerRepository peerrepo;
    @Autowired
    DBManager dbmanager;
    @Autowired
    AsyncExecutors async;

    @Scheduled(fixedRate = 3000)
    public void scanNeighborhoodAndReactOnChanges() {
        logger.info("scanNeighborhoodAndReactOnChanges: " + dateFormat.format(new Date()));
        List<Peer> freshlist = Util.getNeighbors();
        List<Peer> existinglist = peerrepo.findAll();
        List<Peer> addlist = new ArrayList<>();
        List<Peer> dellist = new ArrayList<>();
        for (Peer newpeer : freshlist) {
            if (!existinglist.contains(newpeer)) {
                addlist.add(newpeer);
            }
        }//for        
        for (Peer oldpeer : existinglist) {
            if (!freshlist.contains(oldpeer)) {
                dellist.add(oldpeer);
            }
        }//for         
        if (addlist.size() > 0 || dellist.size() > 0) {
            //update database
            dbmanager.updatePeers(addlist, dellist);
            //notify gateway
            CommandUnicastUpdatePeers cup = new CommandUnicastUpdatePeers(addlist, dellist);
            async.notifyGatewayForPeerChanges(cup);
        }
    }//EoM

}//EoC