package eu.orchestrator.iotstack.iotagent.rest;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.dao.DBManager;
import eu.orchestrator.iotstack.transfer.CommandBroadcastUpdateGateway;
import eu.orchestrator.iotstack.transfer.CommandUnicastUpdatePeers;
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
    public String updatePeers(@RequestBody CommandUnicastUpdatePeers updatecommand) {
        logger.info("Rest updatePeers received");
        dbmanager.updatePeers(updatecommand.getAddlist(), updatecommand.getDellist());  //effective only to gateway
        logger.info("Rest updatePeers executed");
        return "ok";
    }//EoM

    @RequestMapping(value = "/gateway", method = RequestMethod.POST)
    public String updateGateway(@RequestBody CommandBroadcastUpdateGateway cug) {
        logger.info("Rest updateGateway received");
        dbmanager.updateGateway(cug);
        dbmanager.forwardupdateGatewayCommand(cug);
        logger.info("Rest updateGateway executed");
        return "ok";
    }//EoM    
    
    //-----SPI logic in order to be integrated in the MAESTRO Orchestrator-----
    
    @RequestMapping(value = "/validatecredentials", method = RequestMethod.POST)
    public String validatecredentials(@RequestBody CommandBroadcastUpdateGateway cug) {
        logger.info("Rest validatecredentials received");
        logger.info("Rest validatecredentials executed");
        return "ok";
    }//EoM      
    
    @RequestMapping(value = "/instances", method = RequestMethod.GET)
    public String getinstances(@RequestBody CommandBroadcastUpdateGateway cug) {
        logger.info("Rest getinstances received");
        logger.info("Rest getinstances executed");
        return "ok";
    }//EoM      
    
    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    public String bootInstance(@RequestBody CommandBroadcastUpdateGateway cug) {
        logger.info("Rest bootInstance received");
        logger.info("Rest bootInstance executed");
        return "ok";
    }//EoM      
    
    @RequestMapping(value = "/instance", method = RequestMethod.DELETE)
    public String removeInstance(@RequestBody CommandBroadcastUpdateGateway cug) {
        logger.info("Rest removeInstance received");
        logger.info("Rest removeInstance executed");
        return "ok";
    }//EoM      

    @RequestMapping(value = "/resources", method = RequestMethod.GET)
    public String getResources(@RequestBody CommandBroadcastUpdateGateway cug) {
        logger.info("Rest getResources received");
        logger.info("Rest getResources executed");
        return "ok";
    }//EoM      
        
    
//    SPIResponse validateCredentials(CredentialsModel credentials);

//    SPIResponse getInstances(CredentialsModel credentials);   //used to get the interal IPs of booted VMs/containers-components

//    SPIResponse bootInstance(CredentialsModel credentials, ImageModel image, FlavorModel flavor, InstanceModel instance);     //spawn container business logic

//    SPIResponse removeInstance(CredentialsModel credentials, InstanceModel instance);    //decommission business logic

//    SPIResponse getResources(CredentialsModel credentials);       //aggregate resources & topology for IoT

    
}//EoC
