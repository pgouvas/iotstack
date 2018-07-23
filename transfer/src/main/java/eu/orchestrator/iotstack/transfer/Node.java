package eu.orchestrator.iotstack.transfer;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Node {

    private String id;
    private String gateway;
    private String osarch;
    private String osname;    
    private Date bootdate;
    
    public Node() {
    }    
    
    public Node(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        final Node other = (Node) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }         
    
}
