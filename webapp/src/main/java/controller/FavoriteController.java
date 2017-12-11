package controller;

import dao.FavoriteDao;
import dao.FavoriteMetricDao;
import dao.UserDao;
import entity.FavoriteEntity;
import entity.FavoriteMetricEntity;
import entity.SimpleProperty;
import entity.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import provider.MongoDataProvider;
import provider.OpType;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by chao on 2017-11-24.
 */
@Path("/favorite")
@Produces(MediaType.APPLICATION_JSON)
public class FavoriteController {

    @Autowired
    private FavoriteDao favoriteDao;

    @Autowired
    private FavoriteMetricDao favoriteMetricDao;

    @Autowired
    private UserDao userDao;

    private double adjustRatio = 0.05;

    private double yearAdjust = 0.005;

    private String delimiter = "\\|:\\|";

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

    @GET
    @Path("/recommend/{uid}")
    public Response getRecommend(@PathParam("uid") int uid){
        UserEntity userEntity = userDao.findById(uid);
        if (userEntity == null){
            return new Response(0, "Couldn't find the corresponding user", null);
        }

        if (userEntity.getFavoriteMetric() == null){
            return new Response(0, "The user hasn't any favorite house", null);
        }

        List<FavoriteEntity> favoriteEntityList = favoriteDao.findByUid(uid);
        List<String> propertyIdList = new ArrayList<>();
        if (favoriteEntityList != null && !favoriteEntityList.isEmpty()) {
            for (FavoriteEntity fe : favoriteEntityList) {
                propertyIdList.add(fe.getPropertyId());
            }
        }

        Document conditions = generateMongoConditions(userEntity.getFavoriteMetric());
        Queue<Object> mongodata = new ConcurrentLinkedQueue<>(MongoDataProvider.provideData(OpType.FIND, Arrays.asList(conditions), null));

        for (Object property : mongodata){
            if (propertyIdList.contains(((SimpleProperty)property).getPropertyId())){
                mongodata.remove(property);
            }
        }

        return new Response(1, null, mongodata);
    }

    @GET
    @Path("/property/{uid}")
    public Response getFavoriteProperty(@PathParam("uid") int uid){
        UserEntity userEntity = userDao.findById(uid);
        List<FavoriteEntity> favoriteEntityList = favoriteDao.findByUid(uid);
        List<String> propertyIdList = new ArrayList<>();
        if (favoriteEntityList != null && !favoriteEntityList.isEmpty()) {
            for (FavoriteEntity fe : favoriteEntityList) {
                propertyIdList.add(fe.getPropertyId());
            }
        }
        if (userEntity == null){
            return new Response(0, "Couldn't find the corresponding user", null);
        }
        return new Response(1, null, MongoDataProvider.provideData(OpType.FIND, Arrays.asList(new Document("propertyId", new Document("$in",propertyIdList))), null));

    }

    @POST
    @Path("/create")
    public Response createFavorite(FavoriteParam favoriteParam){
        if (favoriteParam.getUid() <= 0 || favoriteParam.getpropertyid() == null){
            return new Response(0, "The user id or property ID couldn't be null", null);
        }

        List<Object> properties = MongoDataProvider.provideData(OpType.FIND,new ArrayList<>(Arrays.asList(new Document("propertyId", favoriteParam.getpropertyid()))),null );
        if (properties.isEmpty()){
            return new Response(0, "Could not find the according property", null);
        }

        if (favoriteDao.findByUidAndPropertyId(favoriteParam.getUid(),favoriteParam.getpropertyid()) != null){
            return new Response(0, "Couldn't save duplicated user propertyId pair", null);
        }

        SimpleProperty property = (SimpleProperty) properties.get(0);

        FavoriteEntity newFavorite = new FavoriteEntity();
        newFavorite.setUid(favoriteParam.getUid());
        newFavorite.setPropertyId(favoriteParam.getpropertyid());
        UserEntity userEntity;
        FavoriteMetricEntity favoriteMetricEntity = null;
        if ((userEntity = userDao.findById(favoriteParam.getUid()))!= null){
            if (userEntity.getFavoriteMetric() != null){
                favoriteMetricEntity = userEntity.getFavoriteMetric();
            }else {
                favoriteMetricEntity = initialFavoriteMetric();
                favoriteMetricEntity.setUser(userEntity);
            }
            compareAndSetFavoriteMetric(favoriteMetricEntity, property);
            saveAndUpdateFavorite(favoriteMetricEntity, newFavorite);
        }
        return new Response(1, "Create succeeded", null);
    }

    @POST
    @Path("/remove")
    public Response removeFavorite(FavoriteParam favoriteParam){

        if (favoriteParam.getUid() <= 0 || favoriteParam.getpropertyid() == null){
            return new Response(0, "The user id or property ID couldn't be null", null);
        }

        int uid = favoriteParam.getUid();
        String propertyId = favoriteParam.getpropertyid();
        FavoriteEntity favoriteEntity;

        UserEntity userEntity = userDao.findById(uid);
        if (userEntity == null){
            return new Response(0, "Couldn't find the corresponding user", null);
        }

        if ((favoriteEntity =favoriteDao.findByUidAndPropertyId(uid,propertyId)) == null){
            return new Response(0, "Couldn't find the corresponding user propertyId pair", null);
        }

        List<FavoriteEntity> favoriteEntityList = favoriteDao.findByUid(uid);
        FavoriteMetricEntity favoriteMetricEntity = userEntity.getFavoriteMetric();
        List<String> propertyIdList = new ArrayList<>();
        if (favoriteEntityList != null && !favoriteEntityList.isEmpty()) {
            for (FavoriteEntity fe : favoriteEntityList) {
                if (!fe.getPropertyId().equals(propertyId)) {
                    propertyIdList.add(fe.getPropertyId());
                }
            }
        }

        if (!propertyIdList.isEmpty()) {
            FavoriteMetricEntity updatedFavoriteMetric = initialFavoriteMetric();
            List<Object> favoriteProperties = MongoDataProvider.provideData(OpType.FIND, Arrays.asList(new Document("propertyId", new Document("$in",propertyIdList))), null);
            for (Object favoriteProperty : favoriteProperties) {
                compareAndSetFavoriteMetric(updatedFavoriteMetric, (SimpleProperty) favoriteProperty);
            }
            copyFavoriteMetric(favoriteMetricEntity, updatedFavoriteMetric);
            removeAndUpdateFavorite(favoriteMetricEntity, favoriteEntity);
        }else{
            removeFavorite(favoriteMetricEntity, favoriteEntity);
        }
        return new Response (1, "Operation succeeded", null);
    }

    @GET
    @Path("/agent/{uid}")
    public Response getAgentList(@PathParam("uid") Integer uid){
        UserEntity userEntity = userDao.findById(uid);
        if (userEntity == null){
            return new Response(0, "Couldn't find the corresponding user", null);
        }

        if (userEntity.getFavoriteMetric() == null){
            return new Response(0, "The user hasn't any favorite house", null);
        }

        return new Response(
                1,
                null,
                MongoDataProvider.provideData(OpType.GROUP,
                        Arrays.asList(
                                new Document("$match", generateMongoConditions(userEntity.getFavoriteMetric())),
                                new Document("$group",new Document("_id", "$brokers.name").append("total", new Document("$sum", 1))),
                                new Document("$sort", new Document("total", -1))
                        ),
                        10));
    }

    @GET
    @Path("/firm/{uid}")
    public Response getFirmList(@PathParam("uid") Integer uid){
        UserEntity userEntity = userDao.findById(uid);
        if (userEntity == null){
            return new Response(0, "Couldn't find the corresponding user", null);
        }

        if (userEntity.getFavoriteMetric() == null){
            return new Response(0, "The user hasn't any favorite house", null);
        }

        return new Response(
                1,
                null,
                MongoDataProvider.provideData(OpType.GROUP,
                        Arrays.asList(
                                new Document("$match", generateMongoConditions(userEntity.getFavoriteMetric())),
                                new Document("$group",new Document("_id", "$firm").append("total", new Document("$sum", 1))),
                                new Document("$sort", new Document("total", -1))
                        ),
                        10));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAndUpdateFavorite(FavoriteMetricEntity favoriteMetricEntity, FavoriteEntity favoriteEntity){
        favoriteDao.save(favoriteEntity);
        favoriteMetricDao.save(favoriteMetricEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeAndUpdateFavorite(FavoriteMetricEntity favoriteMetricEntity, FavoriteEntity favoriteEntity){
        favoriteMetricDao.save(favoriteMetricEntity);
        favoriteDao.delete(favoriteEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeFavorite(FavoriteMetricEntity favoriteMetricEntity, FavoriteEntity favoriteEntity){
        favoriteMetricDao.delete(favoriteMetricEntity);
        favoriteDao.delete(favoriteEntity);
    }

    private void compareAndSetFavoriteMetric(FavoriteMetricEntity favoriteMetricEntity,  SimpleProperty property){
        Set<String> type;
        Set<String> region;
        if (property.getType() != null && !property.getType().isEmpty()){
            if (favoriteMetricEntity.getType() != null && !favoriteMetricEntity.getType().isEmpty()){
                type = new HashSet<>(Arrays.asList(favoriteMetricEntity.getType().split(delimiter)));
            }else{
                type = new HashSet<>();
            }
            type.add(property.getType());
            favoriteMetricEntity.setType(StringUtils.join(type.toArray(), "|:|"));
        }

        if (favoriteMetricEntity.getRegion() != null && !favoriteMetricEntity.getRegion().isEmpty()){
            region = new HashSet<>(Arrays.asList(favoriteMetricEntity.getRegion().split(delimiter)));
        } else {
            region = new HashSet<>();
        }

        if (property.getSublocality() != null && !property.getSublocality().isEmpty()){
            region.add(property.getSublocality());
        }

        if (property.getLocality() != null && !property.getLocality().isEmpty() && !property.getLocality().equalsIgnoreCase("Montréal")){
            region.add(property.getLocality());
        }

        if (!region.isEmpty()){
            favoriteMetricEntity.setRegion(StringUtils.join(region.toArray(), "|:|"));
        }

        if(property.getArea() != null){
            if (property.getArea() < favoriteMetricEntity.getArea()){
                favoriteMetricEntity.setArea(property.getArea());
            }
        }

        if (property.getPrice() != null){
            if (property.getPrice() > favoriteMetricEntity.getMaxPrice()){
                favoriteMetricEntity.setMaxPrice(property.getPrice());
            }
        }

        if (property.getRooms().getBedroom() != null){
            if (property.getRooms().getBedroom() < favoriteMetricEntity.getBedroom()){
                favoriteMetricEntity.setBedroom(property.getRooms().getBedroom());
            }
        }

        if(property.getRooms().getBathroom() != null){
            if (property.getRooms().getBathroom() < favoriteMetricEntity.getBathroom()){
                favoriteMetricEntity.setBathroom(property.getRooms().getBathroom());
            }
        }

        if (property.getYear() != null){
            if (property.getYear() < favoriteMetricEntity.getYear()){
                favoriteMetricEntity.setYear(property.getYear());
            }
        }
    }

    private FavoriteMetricEntity initialFavoriteMetric(){
        FavoriteMetricEntity favoriteMetricEntity = new FavoriteMetricEntity();
        favoriteMetricEntity.setYear(2020);
        favoriteMetricEntity.setBathroom(100);
        favoriteMetricEntity.setBedroom(100);
        favoriteMetricEntity.setMaxPrice(0.0);
        favoriteMetricEntity.setArea(100000000.0);
        favoriteMetricEntity.setType("");
        favoriteMetricEntity.setRegion("");
        return favoriteMetricEntity;
    }

    private void copyFavoriteMetric(FavoriteMetricEntity target, FavoriteMetricEntity source){
        target.setRegion(source.getRegion());
        target.setType(source.getType());
        target.setArea(source.getArea());
        target.setMaxPrice(source.getMaxPrice());
        target.setBedroom(source.getBedroom());
        target.setBathroom(source.getBathroom());
        target.setYear(source.getYear());
    }

    private Document generateMongoConditions(FavoriteMetricEntity favoriteMetric){
        Document conditions = new Document();
        if(favoriteMetric.getMaxPrice() != null){
            conditions.append("price", new Document().append("$lte", favoriteMetric.getMaxPrice() * (1 + adjustRatio)));
        }

        if(favoriteMetric.getArea() != null){
            conditions.append("area", new Document().append("$gte", favoriteMetric.getArea() * (1 - adjustRatio)));
        }

        if(favoriteMetric.getYear() != null){
            conditions.append("year", new Document().append("$gte", favoriteMetric.getYear() * (1 - yearAdjust)));
        }

        if (favoriteMetric.getBedroom() != null){
            conditions.append("rooms.bedroom", new Document().append("$gte", favoriteMetric.getBedroom()));
        }

        if (favoriteMetric.getBathroom() != null){
            conditions.append("rooms.bathroom", new Document().append("$gte", favoriteMetric.getBathroom()));
        }

        if(favoriteMetric.getType() != null){
            conditions.append("type", new Document("$in", Arrays.asList(favoriteMetric.getType().split(delimiter))));
        }

        if (favoriteMetric.getRegion() != null){
            conditions.append(
                    "$or", Arrays.asList(
                    new Document("locality", new Document("$in", Arrays.asList(favoriteMetric.getRegion().split(delimiter)))),
                    new Document("sublocality", new Document("$in", Arrays.asList(favoriteMetric.getRegion().split(delimiter))))
            ));
        }
        return conditions;
    }
}

