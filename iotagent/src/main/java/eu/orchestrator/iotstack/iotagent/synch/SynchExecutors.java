package eu.orchestrator.iotstack.iotagent.synch;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.transfer.entities.iotstack.IoTBootRequest;
import eu.orchestrator.transfer.entities.iotstack.Peer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Service
public class SynchExecutors {
    
    @Autowired
    AsyncExecutors asynch;
    
    private static final Logger logger = Logger.getLogger(SynchExecutors.class.getName());

    public String handleDeployRequest(IoTBootRequest request){
        String ret="Error";
        //Step 1 - Configure Host entry for nexus 
        Util.setupHosts(request.getNexusIPv6(), request.getMasterIPv6());
        //Step 2 - Configure Docker
        Util.setupDocker();
        //Step 2 - Configure Consul
        Util.setupConsul(request.getMasterIPv6(), IoTAgent.nodeid, request.getGraphID().toLowerCase(), request.getGraphInstanceID().toLowerCase() , request.getComponentNodeID().toLowerCase(), request.getComponentNodeInstanceID().toLowerCase());
        //Step 3 - Configure Netadata
        Util.setupNetdata(request.getGraphID().toLowerCase(), request.getGraphInstanceID().toLowerCase(), request.getComponentNodeID().toLowerCase());
        //Step 4 - Call Async to start agent
        asynch.bootAgent(request);
        
        return IoTAgent.nodeid;
    }//EoM
    
    
    public List<Peer> getNodeConfigurationState(List<Peer> addlist) {
        List<Peer> retlist = new ArrayList<>();
        //define an executor pool
        ExecutorService executor = Executors.newWorkStealingPool();
        //create callables
        List<Callable<Peer>> callables = new ArrayList<>();
        for (Peer peer : addlist) {
            callables.add(() -> {                                               //TODO pass the arguments correctly from fragments
                boolean isactive = Util.invokeRestGetNodeId(peer.getTonode());
                peer.setIsactive(isactive);
                return peer;
            });
        }//for load callables

        //Invoke
        try {
            executor.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .forEach(retlist::add);

        } catch (InterruptedException ex) {
            logger.severe(ex.getMessage());
        }

        return retlist;
    }//EoM getNodeConfigurationState

}//EoC
