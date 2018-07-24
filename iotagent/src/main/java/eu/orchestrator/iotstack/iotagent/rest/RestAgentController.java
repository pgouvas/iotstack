package eu.orchestrator.iotstack.iotagent.rest;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.dao.DBManager;
import eu.orchestrator.iotstack.transfer.CommandBroadcastUpdateGateway;
import eu.orchestrator.iotstack.transfer.CommandUnicastUpdatePeers;
import eu.orchestrator.iotstack.transfer.Credentials;
import eu.orchestrator.iotstack.transfer.InstanceModel;
import eu.orchestrator.iotstack.transfer.Nodestat;
import eu.orchestrator.iotstack.transfer.ResourceModel;
import eu.orchestrator.iotstack.transfer.ResponseCode;
import eu.orchestrator.iotstack.transfer.RestResponse;
import eu.orchestrator.iotstack.transfer.Topology;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
        dbmanager.updatePeersRemote(updatecommand);  //effective only to gateway
        logger.info("Rest updatePeers executed");
        return "ok";
    }//EoM

    @RequestMapping(value = "/gateway", method = RequestMethod.POST)
    public String updateGateway(@RequestBody CommandBroadcastUpdateGateway cug) {
        logger.info("Rest updateGateway received");
        dbmanager.updateGateway(cug);
//        dbmanager.forwardupdateGatewayCommand(cug);
        logger.info("Rest updateGateway executed");
        return "ok";
    }//EoM    

    @RequestMapping(value = "/nodestats", method = RequestMethod.GET)
    public RestResponse getNodestats() {
        RestResponse<Nodestat> response = new RestResponse();
        logger.info("Rest getNodestats received");
        Nodestat nodestat = dbmanager.getNodestats();
        response.setRescode(ResponseCode.SUCCESS);
        response.setResobject(nodestat);
        logger.info("Rest getNodestats executed");
        return response;
    }//EoM        
    
    
    //-----SPI logic in order to be integrated in the MAESTRO Orchestrator-----
    
    @RequestMapping(value = "/validatecredentials", method = RequestMethod.POST)
    public RestResponse validatecredentials(@RequestBody Credentials credentials) {
        RestResponse response = new RestResponse();
        logger.info("Rest validatecredentials received");
        response = dbmanager.validatecredentials(credentials);
        logger.info("Rest validatecredentials executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/instances", method = RequestMethod.GET)
    public RestResponse getinstances(@RequestBody Credentials credentials) {
        RestResponse response = new RestResponse();
        logger.info("Rest getinstances received");
        List<InstanceModel> instances = new ArrayList<>();
        response.setRescode(ResponseCode.SUCCESS);
        response.setResobject(response);
        logger.info("Rest getinstances executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    public RestResponse bootInstance(@RequestBody Credentials credentials,@RequestBody InstanceModel instance) {
        RestResponse response = new RestResponse();
        logger.info("Rest bootInstance received");
        response.setRescode(ResponseCode.SUCCESS);
        logger.info("Rest bootInstance executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/instance/{instanceid}", method = RequestMethod.DELETE)
    public RestResponse removeInstance(@RequestBody Credentials credentials,@PathVariable String instanceid) {
        RestResponse response = new RestResponse();
        logger.info("Rest removeInstance received");
        response.setRescode(ResponseCode.SUCCESS);
        logger.info("Rest removeInstance executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/resources", method = RequestMethod.GET)
    public RestResponse getResources(@RequestBody Credentials credentials) {    
        RestResponse response = new RestResponse();
        logger.info("Rest getResources received");
        ResourceModel resourcemodel = dbmanager.getResources();
        response.setRescode(ResponseCode.SUCCESS);
        response.setResobject(resourcemodel);
        logger.info("Rest getResources executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/topology", method = RequestMethod.GET)
    public RestResponse getTopology(@RequestBody Credentials credentials) {                                 //
        RestResponse response = new RestResponse();
        logger.info("Rest getTopology received");
        Topology topology = dbmanager.getTopology();
        response.setRescode(ResponseCode.SUCCESS);
        response.setResobject(topology);
        logger.info("Rest getTopology executed");
        return response;
    }//EoM     
    
//    SPIResponse validateCredentials(CredentialsModel credentials);
//    SPIResponse getInstances(CredentialsModel credentials);   //used to get the interal IPs of booted VMs/containers-components
//    SPIResponse bootInstance(CredentialsModel credentials, ImageModel image, FlavorModel flavor, InstanceModel instance);     //spawn container business logic
//    SPIResponse removeInstance(CredentialsModel credentials, InstanceModel instance);    //decommission business logic
//    SPIResponse getResources(CredentialsModel credentials);       //aggregate resources & topology for IoT

}//EoC
