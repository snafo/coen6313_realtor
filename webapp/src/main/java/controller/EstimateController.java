package controller;

import org.bson.Document;
import provider.MongoDataProvider;
import provider.OpType;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

@Path("/estimate")
public class EstimateController {
    private double latAdj = 0.01;
    private double lngAdj = 0.005;

    @Path("/get")
    public Response getEstimation(
            @QueryParam("lat") Double lat,
            @QueryParam("lng") Double lng,
            @QueryParam("type") String type,
            @QueryParam("area") Double area,
            @QueryParam("year") Integer year,
            @QueryParam("bedroom") Integer bedroom,
            @QueryParam("bathroom") Integer bathroom
    ){
        Document conditions = new Document();

        conditions.append("location.lat", new Document("$gte", lat - latAdj));
        conditions.append("location.lat", new Document("$lte", lat + latAdj));
        conditions.append("location.lng", new Document("$gte", lng - lngAdj));
        conditions.append("location.lng", new Document("$lte", lng + lngAdj));

        List<Object> propertyList = MongoDataProvider.provideData(OpType.FIND, Arrays.asList(conditions),null);
        return new Response(1, null, null);
    }
}
