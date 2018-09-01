package eu.orchestrator.iotstack.iotagent.synch;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.async.AsyncExecutors;
import eu.orchestrator.iotstack.iotagent.dao.NodestatRepository;
import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.transfer.entities.iotstack.IoTBootRequest;
import eu.orchestrator.transfer.entities.iotstack.Nodestat;
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
    @Autowired
    NodestatRepository nodestatrepo;    
    
    
    private static final Logger logger = Logger.getLogger(SynchExecutors.class.getName());
    
    //TODO create exception
    public String findAvailableResourceAndDeploy(IoTBootRequest request){
        String deploymentid = "";
        List<Nodestat> availableresources = nodestatrepo.findAllAvailable();
        if (!availableresources.isEmpty()){
            Nodestat selectednode = availableresources.get(0);
            deploymentid = selectednode.getNodeid()+"_"+request.getGraphID()+"_"+request.getGraphInstanceID()+"_"+request.getComponentNodeID()+"_"+request.getComponentNodeInstanceID();
            //save
            selectednode.setContainer(deploymentid);
            nodestatrepo.update(selectednode);
            //communicate to node and trigger deployment
            asynch.forwardDeploymentRequestToNode(selectednode.getNodeid(), request);
        }//if
        return deploymentid;
    }//EoM
    
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
            
    
    public List<Peer> getNodeState(List<Peer> addlist) {
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
