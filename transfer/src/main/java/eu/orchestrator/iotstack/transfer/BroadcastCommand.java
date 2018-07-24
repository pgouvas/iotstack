package eu.orchestrator.iotstack.transfer;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Panagiotis Gouvas
 */
public class BroadcastCommand implements Serializable{
 
    private String cid;     //command identifier
    private Date cdate;   //command date  
    private String initid;   //command date  
    private String solicitorid;    

    public BroadcastCommand() {
    }

    public BroadcastCommand(String initid, String solicitorid) {
        Random rand = new Random();
        this.cid = (""+rand.nextFloat()).substring(2, 7);
        this.cdate = new Date();        
        this.initid = initid;
        this.solicitorid = solicitorid;
    }    
    
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public String getInitid() {
        return initid;
    }

    public void setInitid(String initid) {
        this.initid = initid;
    }

    public String getSolicitorid() {
        return solicitorid;
    }

    public void setSolicitorid(String solicitorid) {
        this.solicitorid = solicitorid;
    }    
    
}//EoC
