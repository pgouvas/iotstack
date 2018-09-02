package eu.orchestrator.iotstack.iotagent.scheduled;

import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.iotagent.dao.PeerRepository;
import eu.orchestrator.iotstack.iotagent.dao.DBManager;
import eu.orchestrator.iotstack.iotagent.synch.SynchExecutors;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.transfer.entities.iotstack.CommandUnicastUpdatePeers;
import eu.orchestrator.transfer.entities.iotstack.Peer;
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
    @Autowired
    SynchExecutors synch;
    
    
    @Scheduled(fixedRate = 5000)
    public void scanNeighborhoodAndReactOnChanges() {
        logger.info("scanNeighborhoodAndReactOnChanges: " + dateFormat.format(new Date()));
        List<Peer> freshlist = Util.getNeighbors();     //get from physical
        List<Peer> existinglist = peerrepo.findAllActive();   //get active
        List<Peer> addlist = new ArrayList<>();
        List<Peer> dellist = new ArrayList<>();
        
        //prevent doubles logic
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
            //check which of the nodes are active (i.e. agent is running)
            addlist = synch.getNodeStateForPeers(addlist);  //logger.info("status of added: "+addlist);
            //update database
            dbmanager.updatePeersLocal(addlist, dellist);
            //notify gateway
            CommandUnicastUpdatePeers cup = new CommandUnicastUpdatePeers(addlist, dellist);
            async.notifyGatewayForPeerChanges(cup);
        }
    }//EoM

}//EoC
