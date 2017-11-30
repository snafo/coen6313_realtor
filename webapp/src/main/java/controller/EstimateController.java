package controller;

import entity.SimpleProperty;
import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;
import org.bson.Document;
import provider.MongoDataProvider;
import provider.OpType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Path("/estimate")
public class EstimateController {
    private double latAdj = 0.01;
    private double lngAdj = 0.005;
    final OLSMultipleLinearRegression mlRegression = new OLSMultipleLinearRegression();

    @GET
    @Path("/get")
    public Response estimatePrice(
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
        int size = 0 ;
        for (Object property : propertyList) {
            if (((SimpleProperty) property).getPrice() != null &&
                    ((SimpleProperty) property).getType() !=null &&
                    ((SimpleProperty) property).getArea() != null &&
                    ((SimpleProperty) property).getYear() !=null &&
                    ((SimpleProperty) property).getRooms().getBedroom() !=null &&
                    ((SimpleProperty) property).getRooms().getBathroom() !=null &&
                    ((SimpleProperty) property).getRooms().getBedroom() !=0 &&
                    ((SimpleProperty) property).getRooms().getBathroom() !=0 ) {
                size++;
            }
        }

        // 5 parameters: type, area, year, bedroom, bathroom
        double[][] x_training  = new double[size][5];
        double[] y_training = new double[size];
        int i =0;
        for (Object property : propertyList) {
            if (((SimpleProperty) property).getPrice() != null &&
                    ((SimpleProperty) property).getType() !=null &&
                    ((SimpleProperty) property).getArea() != null &&
                    ((SimpleProperty) property).getYear() !=null &&
                    ((SimpleProperty) property).getRooms().getBedroom() !=null &&
                    ((SimpleProperty) property).getRooms().getBathroom() !=null &&
                    ((SimpleProperty) property).getRooms().getBedroom() !=0 &&
                    ((SimpleProperty) property).getRooms().getBathroom() !=0  ) {

                x_training[i] = new double[]{ mapPropertyType((((SimpleProperty) property).getType())),
                                            ((SimpleProperty) property).getArea(),
                                            ((SimpleProperty) property).getYear(),
                                            ((SimpleProperty) property).getRooms().getBedroom(),
                                            ((SimpleProperty) property).getRooms().getBathroom()};
                y_training[i] = ((SimpleProperty) property).getPrice();
                i++;
            }
        }

        if(size < 5){
            return new Response(0, "The training set are too small", null);
        }

        mlRegression.setNoIntercept(true);
        mlRegression.newSampleData(y_training, x_training);

        double[] beta = mlRegression.estimateRegressionParameters();

        double y = beta[0] * mapPropertyType(type) + beta[1] * year + beta[2] * area + beta[3] *bedroom + beta[4]*bathroom;

        HashMap<String, Double> response =new HashMap<>();
        response.put("size", ((double)size));
        response.put("price", y);

        return new Response(1, "Estimate succeeded", response);
    }

    private int mapPropertyType(String type){

        if(type.equalsIgnoreCase("House")){
            return 1;
        }
        else if(type.equalsIgnoreCase("Condo")){
            return 2;
        }
        else if(type.equalsIgnoreCase("Townhouse")){
            return 3;
        }
        else if(type.equalsIgnoreCase("Studio")){
            return 4;
        }
        else{
            return 5;
        }
    }
}
