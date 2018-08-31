package eu.orchestrator.transfer.entities.iotstack;

import java.io.Serializable;

/**
 *
 * @author Panagiotis Gouvas
 */
public class InstanceModel implements Serializable {

    private String id;

    private String name;

    private String imageID;

    private String instanceType;

    private String state;

    private String monitoringState;

    private String providerID;

    private String networkID;

    private String KeyPairName;

    private String userData;

    private String privateIP;

    public InstanceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMonitoringState() {
        return monitoringState;
    }

    public void setMonitoringState(String monitoringState) {
        this.monitoringState = monitoringState;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getNetworkID() {
        return networkID;
    }

    public void setNetworkID(String networkID) {
        this.networkID = networkID;
    }

    public String getKeyPairName() {
        return KeyPairName;
    }

    public void setKeyPairName(String keyPairName) {
        KeyPairName = keyPairName;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getPrivateIP() {
        return privateIP;
    }

    public void setPrivateIP(String privateIP) {
        this.privateIP = privateIP;
    }
}
