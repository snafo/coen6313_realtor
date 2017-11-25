package controller;

import org.springframework.data.jpa.repository.Query;

import javax.ws.rs.QueryParam;

/**
 * Created by qinyu on 2017-11-25.
 */
public class UserParam {
    @QueryParam("name")
    private String name;

    @QueryParam("password")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
