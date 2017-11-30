package controller;

import com.google.gson.Gson;
import entity.SimpleProperty;
import org.bson.Document;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchResponse;
import provider.EsDataProvider;
import provider.MongoDataProvider;
import provider.OpType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
            @QueryParam("region") String region,
            @QueryParam("keywords") String keywords)
    {
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
                        }
                    }
                }else{
                    mongodata.clear();
                }
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

    @GET
    @Path("/getEs")
    public Response getProperty() throws IOException {
        return new Response(1, null, EsDataProvider.provideData("jean talon market"));
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
