package eu.orchestrator.iotstack.transfer;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Panagiotis Gouvas
 */
public class Peer {
    private String fromnode;
    private String tonode;
    private Date registrationdate;

    public Peer() {
    }

    public Peer(String fromnode, String tonode) {
        this.fromnode = fromnode;
        this.tonode = tonode;
    }

    public Peer(String fromnode, String tonode, Date registrationdate) {
        this.fromnode = fromnode;
        this.tonode = tonode;
        this.registrationdate = registrationdate;
    }

    
    
    public String getFromnode() {
        return fromnode;
    }

    public void setFromnode(String fromnode) {
        this.fromnode = fromnode;
    }

    public String getTonode() {
        return tonode;
    }

    public void setTonode(String tonode) {
        this.tonode = tonode;
    }

    public Date getRegistrationdate() {
        return registrationdate;
    }

    public void setRegistrationdate(Date registrationdate) {
        this.registrationdate = registrationdate;
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.fromnode);
        hash = 79 * hash + Objects.hashCode(this.tonode);
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
        final Peer other = (Peer) obj;
        if (!Objects.equals(this.fromnode, other.fromnode)) {
            return false;
        }
        if (!Objects.equals(this.tonode, other.tonode)) {
            return false;
        }
        return true;
    }
    
   
}
