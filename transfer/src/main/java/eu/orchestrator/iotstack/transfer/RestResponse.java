package eu.orchestrator.iotstack.transfer;

/**
 *
 * @author Panagiotis Gouvas
 */
public class RestResponse {

    private int rescode;            //200 ok
    private String message;
    private Object resobject;

    public RestResponse() {
    }
            
    public RestResponse(int rescode) {
        this.rescode = rescode;
    }

    public RestResponse(int rescode, String message) {
        this.rescode = rescode;
        this.message = message;
    }

    public RestResponse(int rescode, String message, Object resobject) {
        this.rescode = rescode;
        this.message = message;
        this.resobject = resobject;
    }    
    
    public int getRescode() {
        return rescode;
    }    
    
    public void setRescode(int rescode) {
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
