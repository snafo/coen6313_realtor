package controller;

import com.google.gson.Gson;
import org.bson.Document;
import provider.MongoDataProvider;
import provider.OpType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by qinyu on 2017-11-02.
 */
@Path("/property")
@Produces(MediaType.APPLICATION_JSON)
public class Controller {
    Gson gson = new Gson();

    @GET
    @Path("/get")
    public Response getProperty(
            @QueryParam("price") String price,
            @QueryParam("area") String area,
            @QueryParam("type") String type,
            @QueryParam("year") String year,
            @QueryParam("source") String source,
            @QueryParam("limit") Integer limit,
            @QueryParam("bedroom") String bedroom,
            @QueryParam("bathroom") String bathroom,
            @QueryParam("sublocality") String sublocality)
    {
        Document conditions = new Document();

        if(price != null){
            conditions.append("price", parseCondition(price));
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

        if (bedroom != null){
            conditions.append("sublocality", parseCondition(sublocality));
        }

        return new Response(1, "succeeded", MongoDataProvider.provideData(OpType.FIND, Arrays.asList(conditions), limit));
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
