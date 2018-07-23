package eu.orchestrator.iotstack.transfer;

/**
 *
 * @author Panagiotis Gouvas
 */
public class RestResponse {

    private ResponseCode rescode;            //200 ok
    private String message;
    private Object resobject;

    public RestResponse() {
    }
            
    public RestResponse(ResponseCode rescode) {
        this.rescode = rescode;
    }

    public RestResponse(ResponseCode rescode, String message) {
        this.rescode = rescode;
        this.message = message;
    }

    public RestResponse(ResponseCode rescode, String message, Object resobject) {
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

    public Object getResobject() {
        return resobject;
    }

    public void setResobject(Object resobject) {
        this.resobject = resobject;
    }   
    
}
