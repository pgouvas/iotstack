package eu.orchestrator.iotstack.transfer;

/**
 *
 * @author Panagiotis Gouvas
 */
public class CommandUpdateGateway {
    
    private String gatewayid;

    public CommandUpdateGateway() {
    }

    public CommandUpdateGateway(String gatewayid) {
        this.gatewayid = gatewayid;
    }    
    
    public String getGatewayid() {
        return gatewayid;
    }

    public void setGatewayid(String gatewayid) {
        this.gatewayid = gatewayid;
    }
    
}//EoC
