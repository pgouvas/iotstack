package eu.orchestrator.iotstack.iotagent.async;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author Panagiotis Gouvas
 */
@Service
public class AsyncExecutors {
    //All methods should be async
    private static final Logger logger = Logger.getLogger(AsyncExecutors.class.getName());
    
    @Async
    public void do1(String user) throws InterruptedException {        
        Thread.sleep(2000L);
    }//EoM
    
    public void do2(String user) throws InterruptedException {        
        Thread.sleep(2000L);
    }//EoM    
    
}
