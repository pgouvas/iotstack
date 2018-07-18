package eu.orchestrator.iotstack.iotagent.rest;

import java.util.logging.Logger;
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
    
    @RequestMapping(value="/nodeid",method = RequestMethod.GET)
    public String index() {
        return "";
    }

    @RequestMapping(value="/echo",method = RequestMethod.GET)
    public String echo() {
        logger.info("Echo request received");
        return "echo";
    }
    
    @RequestMapping(value="/clusterhead",method = RequestMethod.GET)
    public String getClusterhead() {
        return "";
    }
    
    @RequestMapping(value="/clusterhead",method = RequestMethod.POST)
    public String setClusterhead() {
        return "";
    }    
    
    
}//EoC
