package controller;

import dao.FavoriteDao;
import entity.FavoriteEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by chao on 2017-11-24.
 */
@Path("/favorite")
@Produces(MediaType.APPLICATION_JSON)
public class FavoriteController {

    @Autowired
    private FavoriteDao favoriteDao;

    @GET
    @Path("/all")
    public Response getAllFavorite(){
        return new Response (1, null, favoriteDao.findAll());
    }

    @GET
    @Path("/findbyuidcustom")
    public Response getFavoriteByUidCustom(@QueryParam("uid") int uid){
        return new Response (1, null, favoriteDao.findByUid(uid));
    }

    @GET
    @Path("/findbyuid/{uid}")
    public Response getFavoriteByUid(@PathParam("uid") int uid){
        return new Response(1, null, favoriteDao.findByUid(uid));
    }

    @POST
    @Path("/create")
    public Response createFavorite(FavoriteParam favoriteParam){
        if (favoriteParam.getUid() <= 0 || favoriteParam.getPropertyId() == null){
            return new Response(0, "The user id or property ID couldn't be null", null);
        }

        FavoriteEntity newFavorite = new FavoriteEntity();
        newFavorite.setUid(favoriteParam.getUid());
        newFavorite.setPropertyId(favoriteParam.getPropertyId());
        favoriteDao.save(newFavorite);
        return new Response(1, "Create succeeded", null);
    }

}

