package ws;

import com.sun.tracing.dtrace.ProviderAttributes;
import entities.*;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import model.*;

@Path("user")
public class UserRestful{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/finall")
    public Response findAll(){
        UserModel userModel = new UserModel();
        return Response
                .ok()
                .entity(new GenericEntity<List<UserEntity>>(userModel.findAll()){
                })
                .header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS,HEAD").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/find/{id}")
    public Response find(@PathParam(value = "id") String id){
        UserModel userModel = new UserModel();
        UserEntity user  = userModel.find(Integer.valueOf(id));
        return Response
                .ok()
                .entity(new GenericEntity<UserEntity>(user){
                })
                .header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS,HEAD").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response create(UserEntity user){
        UserModel userModel = new UserModel();
        userModel.create(user);
        return Response
                .ok()
                .header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS,HEAD").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    public Response delete(@PathParam(value = "id") String id){
        UserModel userModel = new UserModel();
        userModel.delete(userModel.find(Integer.valueOf(id)));
        return Response
                .ok()
                .header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS,HEAD").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/edit")
    public Response edit(UserEntity user){
        UserModel userModel = new UserModel();
        userModel.update(user);
        return Response
                .ok()
                .header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS,HEAD").build();
    }


}