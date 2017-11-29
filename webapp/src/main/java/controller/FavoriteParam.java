package controller;

import javax.ws.rs.QueryParam;

public class FavoriteParam {
    @QueryParam("uid")
    private int uid;

    @QueryParam("propertyid")
    private String propertyid;


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getpropertyid() {
        return propertyid;
    }

    public void setpropertyid(String propertyid) {
        this.propertyid = propertyid;
    }
}
