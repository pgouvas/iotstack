package eu.orchestrator.iotstack.transfer;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Nodestat implements Serializable{

    private String nodeid;
    private String gateway;
    private String osarch;
    private String osname;    
    private Date bootdate;
    private int vcpus;
    private int cpuspeed;
    private int totalmemory;    
    private Date checkdate;    
    
    public Nodestat() {
    }    

    public Nodestat(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }
    
    
    public Date getBootdate() {
        return bootdate;
    }

    public void setBootdate(Date bootdate) {
        this.bootdate = bootdate;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getOsarch() {
        return osarch;
    }

    public void setOsarch(String osarch) {
        this.osarch = osarch;
    }

    public String getOsname() {
        return osname;
    }

    public void setOsname(String osname) {
        this.osname = osname;
    }

    public int getVcpus() {
        return vcpus;
    }

    public void setVcpus(int vcpus) {
        this.vcpus = vcpus;
    }

    public int getCpuspeed() {
        return cpuspeed;
    }

    public void setCpuspeed(int cpuspeed) {
        this.cpuspeed = cpuspeed;
    }

    public int getTotalmemory() {
        return totalmemory;
    }

    public void setTotalmemory(int totalmemory) {
        this.totalmemory = totalmemory;
    }    

    public Date getCheckdate() {
        return checkdate;
    }

    public void setCheckdate(Date checkdate) {
        this.checkdate = checkdate;
    }    
    
    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Nodestat other = (Nodestat) obj;
        if (!Objects.equals(this.nodeid, other.nodeid)) {
            return false;
        }
        return true;
    }         
    
}
