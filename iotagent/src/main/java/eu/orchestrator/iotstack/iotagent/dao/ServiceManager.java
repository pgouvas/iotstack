package eu.orchestrator.iotstack.iotagent.dao;

import eu.orchestrator.iotstack.iotagent.exception.AgentException;
import eu.orchestrator.iotstack.transfer.Node;
import java.util.Random;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Panagiotis Gouvas
 */
@Component
public class ServiceManager {

    private static final Logger logger = Logger.getLogger(ServiceManager.class.getName());    
    
    @Autowired
    NodeRepository noderepo;    

    @Transactional(rollbackFor = AgentException.class)
    public void transactiontest() throws AgentException {
        logger.info("Test insert");

        Random rand = new Random();
        Node node = new Node(("" + rand.nextInt()).substring(2, 10));
        noderepo.insert(node);
        //throw new AgentException("Agent Exception");
    }//EoM
    
}
