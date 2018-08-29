package eu.orchestrator.iotstack.iotagent.synch;

import eu.orchestrator.iotstack.iotagent.util.Util;
import eu.orchestrator.iotstack.transfer.Peer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Service
public class SynchExecutors {

    private static final Logger logger = Logger.getLogger(SynchExecutors.class.getName());

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
    }//EoM

}//EoC
