package controller;

import com.google.gson.Gson;
import dao.FavoriteDao;
import dao.UserDao;
import entity.FavoriteEntity;
import entity.SimpleProperty;
import entity.UserEntity;
import org.bson.Document;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import provider.EsDataProvider;
import provider.MongoDataProvider;
import provider.OpType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by qinyu on 2017-11-02.
 */
@Path("/property")
@Produces(MediaType.APPLICATION_JSON)
public class Controller {
    Gson gson = new Gson();

    @Autowired
    UserDao userDao;

    @Autowired
    FavoriteDao favoriteDao;

    @GET
    @Path("/get/{username}")
    public Response getProperty(
            @PathParam("username") String username,
            @QueryParam("minPrice") Integer minPrice,
            @QueryParam("maxPrice") Integer maxPrice,
            @QueryParam("area") String area,
            @QueryParam("type") String type,
            @QueryParam("year") String year,
            @QueryParam("source") String source,
            @QueryParam("limit") Integer limit,
            @QueryParam("bedroom") String bedroom,
            @QueryParam("bathroom") String bathroom,
            @QueryParam("region") String region,
            @QueryParam("keywords") String keywords)
    {
        if (username == null){
            return new Response(0, "The user id or property ID couldn't be null", null);
        }

        UserEntity userEntity = userDao.findOneByName(username);
        if (userEntity == null){
            return new Response(0, "Couldn't find the corresponding user", null);
        }

        List<FavoriteEntity> favoriteEntityList = favoriteDao.findByUid(userEntity.getId());
        List<String> propertyIdList = new ArrayList<>();
        if (favoriteEntityList != null && !favoriteEntityList.isEmpty()) {
            for (FavoriteEntity fe : favoriteEntityList) {
                propertyIdList.add(fe.getPropertyId());
            }
        }

        ListenableActionFuture<SearchResponse> esFuture = null;
        List<String> esProperties = null;

        if (keywords != null && !keywords.isEmpty()){
            try {
                esFuture = EsDataProvider.executeQuery(keywords);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Document conditions = new Document();

        if(minPrice != null){
            conditions.append("price", parseCondition("{gte:" + minPrice + "}"));
        }

        if(maxPrice != null){
            conditions.append("price", parseCondition("{lte:" + maxPrice + "}"));
        }

        if(area != null){
            conditions.append("area", parseCondition(area));
        }

        if(year != null){
            conditions.append("year", parseCondition(year));
        }

        if(type != null){
            conditions.append("type", type);
        }

        if(source != null){
            conditions.append("source", source);
        }

        if (bedroom != null){
            conditions.append("rooms.bedroom", parseCondition(bedroom));
        }

        if (bathroom != null){
            conditions.append("rooms.bathroom", parseCondition(bathroom));
        }

        if (region != null){
            conditions.append("$or", Arrays.asList(new Document("locality", region),new Document("sublocality", region)));
        }

        Queue<Object> mongodata = new ConcurrentLinkedQueue<>(MongoDataProvider.provideData(OpType.FIND, Arrays.asList(conditions), limit));
        if(esFuture != null){
            esFuture.actionGet();
            esProperties = EsDataProvider.getData(esFuture);
            if (esProperties != null){
                if (!esProperties.isEmpty()){
                    for (Object property : mongodata){
                        if (!esProperties.contains(((SimpleProperty)property).getPropertyId())){
                            mongodata.remove(property);
                            continue;
                        }
                    }
                }else{
                    mongodata.clear();
                }
            }
        }

        for (Object property : mongodata) {
            if (propertyIdList.contains(((SimpleProperty) property).getPropertyId())) {
                ((SimpleProperty) property).setFavorite(true);
            } else {
                ((SimpleProperty) property).setFavorite(false);
            }
        }

        return new Response(1, "succeeded", mongodata);
    }


    @GET
    @Path("/aggregate")
    public Response getAggregation(
            @QueryParam("price") String price,
            @QueryParam("area") String area,
            @QueryParam("type") String type,
            @QueryParam("year") String year,
            @QueryParam("source") String source)
    {
        List<Document> conditions = new ArrayList<>();

        if(price != null){
            conditions.add(new Document("$match", new Document ("price", parseCondition(price))));
        }

        if(area != null){
            conditions.add(new Document("$match", new Document ("area", parseCondition(area))));
        }

        if(year != null){
            conditions.add(new Document("$match", new Document ("year", parseCondition(year))));
        }

        if(type != null){
            conditions.add(new Document("$match", new Document ("type", type)));
        }

        if(source != null){
            conditions.add(new Document("$match", new Document ("source", source)));
        }

        conditions.add(new Document("$group", new Document("_id", "aggregation").append("count", new Document("$sum", 1))));


        return new Response(1, "succeeded", MongoDataProvider.provideData(OpType.GROUP, conditions, null));
    }

    private Object parseCondition(String input){
        try {
            Map<String, Integer> map = gson.fromJson(input, Map.class);
            Document document = new Document();
            for (Map.Entry<String, Integer> mapEntry : map.entrySet()){
                document.append("$" + mapEntry.getKey(),mapEntry.getValue());
            }
            return document;
        }catch (Exception e){
            return input;
        }
    }
}
