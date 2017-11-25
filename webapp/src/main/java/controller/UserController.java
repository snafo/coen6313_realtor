package controller;

import dao.UserDao;
import entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by qinyu on 2017-11-24.
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
    @Autowired
    private UserDao userDao;

    @GET
    @Path("/all")
    public Response getUsers(){
        return new Response (1, null, userDao.findAll());
    }

    @GET
    @Path("/findbynamecustom")
    public Response getUserByNameCustom(@QueryParam("name") String name){
        return new Response (1, null, userDao.findByNameCustom(name));
    }

    @GET
    @Path("/findbyname/{name}")
    public Response getUserByName(@PathParam("name") String name){
        return new Response(1, null, userDao.findByName(name));
    }

    @POST
    @Path("/create")
    public Response createUser(UserParam userParam){
        if (userParam.getName() == null || userParam.getPassword() == null){
            return new Response(0, "The name or password couldn't be null", null);
        }

        UserEntity newUser = new UserEntity();
        newUser.setName(userParam.getName());
        newUser.setPassword(userParam.getPassword());
        userDao.save(newUser);
        return new Response(1, "Create succeeded", null);
    }

}
