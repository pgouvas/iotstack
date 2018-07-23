package eu.orchestrator.iotstack.iotagent.rest;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.dao.DBManager;
import eu.orchestrator.iotstack.transfer.CommandUpdateGateway;
import eu.orchestrator.iotstack.transfer.CommandUpdatePeers;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Panagiotis Gouvas
 */
@RestController
@RequestMapping("/api/v1")
public class RestAgentController {

    private static final Logger logger = Logger.getLogger(RestAgentController.class.getName());

    @Autowired
    DBManager dbmanager;

    @RequestMapping(value = "/nodeid", method = RequestMethod.GET)
    public String getNodeid() {
        return IoTAgent.nodeid;
    }

    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String echo() {
        logger.info("Echo received");
        return "echo";
    }

    @RequestMapping(value = "/peers", method = RequestMethod.POST)
    public String updatePeers(@RequestBody CommandUpdatePeers updatecommand) {
        logger.info("Rest updatePeers received");
        dbmanager.updatePeers(updatecommand.getAddlist(), updatecommand.getDellist());
        logger.info("Rest updatePeers executed");
        return "ok";
    }//EoM

    @RequestMapping(value = "/gateway", method = RequestMethod.POST)
    public String updateGateway(@RequestBody CommandUpdateGateway cug) {
        logger.info("Rest updateGateway received");
        dbmanager.updateGateway(cug.getGatewayid());
        logger.info("Rest updateGateway executed");
        return "ok";
    }//EoM    
    
}//EoC
