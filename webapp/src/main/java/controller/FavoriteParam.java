package controller;

import javax.ws.rs.QueryParam;

public class FavoriteParam {
    @QueryParam("uid")
    private int uid;

    @QueryParam("propertyId")
    private String propertyId;


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
}
