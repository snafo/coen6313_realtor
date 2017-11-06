package controller;

/**
 * Created by y_qin on 5/8/2017.
 * A class that wraps the messages returned by the api
 */
public class Response {
    private int code;

    private String message;

    private Object payLoad;


    public Response(int code, String message, Object payLoad) {
        this.code = code;
        this.message = message;
        this.payLoad = payLoad;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(Object payLoad) {
        this.payLoad = payLoad;
    }

}
