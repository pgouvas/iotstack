package eu.orchestrator.iotstack.iotagent.rest;

import eu.orchestrator.iotstack.iotagent.IoTAgent;
import eu.orchestrator.iotstack.iotagent.dao.DBManager;
import eu.orchestrator.iotstack.iotagent.synch.SynchExecutors;
import eu.orchestrator.transfer.entities.iotstack.CommandBroadcastUpdateGateway;
import eu.orchestrator.transfer.entities.iotstack.CommandUnicastUpdatePeers;
import eu.orchestrator.transfer.entities.iotstack.Credentials;
import eu.orchestrator.transfer.entities.iotstack.InstanceModel;
import eu.orchestrator.transfer.entities.iotstack.IoTBootRequest;
import eu.orchestrator.transfer.entities.iotstack.IoTRemoveInstance;
import eu.orchestrator.transfer.entities.iotstack.Nodestat;
import eu.orchestrator.transfer.entities.iotstack.ResourceModel;
import eu.orchestrator.transfer.entities.iotstack.ResponseCode;
import eu.orchestrator.transfer.entities.iotstack.RestResponse;
import eu.orchestrator.transfer.entities.iotstack.Topology;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    SynchExecutors synch;
    
//    @Autowired
//    AsyncExecutors asynch;    
    
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
        Nodestat nodestat = dbmanager.getNodestatsForRemoteReporting(); //exclude bandwith rttdelay packetloss
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

    @RequestMapping(value = "/instances", method = RequestMethod.POST)
    public RestResponse getinstancesproxy(@RequestBody Credentials credentials) {
        RestResponse response = new RestResponse();
        logger.info("Rest getinstances received");
        List<InstanceModel> instances = new ArrayList<>();
        response.setRescode(ResponseCode.SUCCESS);
        response.setResobject(response);
        logger.info("Rest getinstances executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    public RestResponse bootInstanceproxy(@RequestBody IoTBootRequest bootrequest) {
        RestResponse response = new RestResponse();
        logger.info("*****Rest BootInstance-Proxy  received");
        
        String deployid = synch.findAvailableResourceAndDeploy(bootrequest);
//        logger.info("Deploymentid: "+deployid);
        response.setRescode(ResponseCode.SUCCESS);
        response.setMessage("Success");
        response.setResobject(""+deployid);
        
        logger.info("****Rest BootInstance-Proxy handled");
        return response;
    }//EoM      
    
    @RequestMapping(value = "/bootinstance", method = RequestMethod.POST)
    public RestResponse bootInstance(@RequestBody IoTBootRequest bootrequest) {
        RestResponse response = new RestResponse();
        logger.info("****Rest bootinstance received");
        
        String success = synch.handleDeployRequest(bootrequest);
        
        response.setRescode(ResponseCode.SUCCESS);
        response.setMessage("Success");
        response.setResobject(""+success);
        
        logger.info("****Rest bootInstance executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/deleteinstance", method = RequestMethod.DELETE)
    public RestResponse removeInstanceproxy(@RequestBody IoTRemoveInstance delrequest) {
        RestResponse response = new RestResponse();
        logger.info("Rest removeInstance received");
        
        response.setRescode(ResponseCode.SUCCESS);
        logger.info("Rest removeInstance executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/resources", method = RequestMethod.POST)
    public RestResponse getResources(@RequestBody Credentials credentials) {    
        RestResponse response = new RestResponse();
        logger.info("Rest getResources received");
        ResourceModel resourcemodel = dbmanager.getResources();
        response.setRescode(ResponseCode.SUCCESS);
        response.setResobject(resourcemodel);
        logger.info("Rest getResources executed");
        return response;
    }//EoM      

    @RequestMapping(value = "/topology", method = RequestMethod.POST)
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
