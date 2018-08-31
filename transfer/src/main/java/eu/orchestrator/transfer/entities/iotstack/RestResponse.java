package eu.orchestrator.transfer.entities.iotstack;

import java.io.Serializable;

/**
 *
 * @author Panagiotis Gouvas
 */
public class RestResponse<R> implements Serializable {

    private ResponseCode rescode;            //200 ok
    private String message;
    private R resobject;

    public RestResponse() {
    }
            
    public RestResponse(ResponseCode rescode) {
        this.rescode = rescode;
    }

    public RestResponse(ResponseCode rescode, String message) {
        this.rescode = rescode;
        this.message = message;
    }

    public RestResponse(ResponseCode rescode, String message, R resobject) {
        this.rescode = rescode;
        this.message = message;
        this.resobject = resobject;
    }    
    
    public ResponseCode getRescode() {
        return rescode;
    }    
    
    public void setRescode(ResponseCode rescode) {
        this.rescode = rescode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public R getResobject() {
        return resobject;
    }

    public void setResobject(R resobject) {
        this.resobject = resobject;
    }   
    
}
