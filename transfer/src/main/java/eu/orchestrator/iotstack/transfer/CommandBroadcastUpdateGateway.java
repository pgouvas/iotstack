package eu.orchestrator.iotstack.transfer;

/**
 *
 * @author Panagiotis Gouvas
 */
public class CommandBroadcastUpdateGateway extends BroadcastCommand {
 
    private String gatewayid;

    public CommandBroadcastUpdateGateway() {
    }

    public CommandBroadcastUpdateGateway(String initid, String solicitorid,String gatewayid) {
        super(initid, solicitorid);
        this.gatewayid = gatewayid;
    }
    
    public String getGatewayid() {
        return gatewayid;
    }

    public void setGatewayid(String gatewayid) {
        this.gatewayid = gatewayid;
    }  
    
}//EoC
