package eu.orchestrator.iotstack.iotagent.dao;

import java.util.Date;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Commandlog {

    private String cid;     //command identifier
    private Date cdate;   //command date  

    public Commandlog() {
    }

    public Commandlog(String cid, Date cdate) {
        this.cid = cid;
        this.cdate = cdate;
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
    
}//EoC
