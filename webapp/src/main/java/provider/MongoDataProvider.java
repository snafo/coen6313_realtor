package provider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import entity.SimpleProperty;
import org.bson.Document;
import java.util.*;

/**
 * Created by qinyu on 2017-11-01.
 */
public class DataProvider {
    public static Gson gson = new Gson();

    public static void main(String[] args){
        DataProvider dataProvider = new DataProvider();
//        List<Object> output = dataProvider.provideData(OpType.GROUP, Arrays.asList(
//                new Document("$match", new Document("price", new Document("$gt", 1000000))),
//                new Document("$group", new Document("_id", 0).append("count", new Document("$sum", 1)))
//        ));
//
//        List<Object> output2 = dataProvider.provideData(OpType.FIND, Arrays.asList(
//                new Document("price", new Document("$lt", 1000000))
//                .append("area", new Document("$gt",  10000))
//        ));
        System.out.print("");

        //        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
//                new Document("$unwind", "$views"),
//                new Document("$match", new Document("views.isActive", true)),
//                new Document("$sort", new Document("views.date", 1)),
//                new Document("$limit", 200),
//                new Document("$project", new Document("_id", 0)
//                        .append("crawler.url", "$views.crawler.url")
//                        .append("date", "$views.date"))
//        ));
    }

    public static List<Object> provideData( OpType opType, List<Document> conditions, Integer limitInput) {
        int limit = 10000;
        if (limitInput != null){
            limit = limitInput;
        }


        // Creating a Mongo client
        try (MongoClient mongo = new MongoClient("localhost", 27017)) {

            // Creating Credentials
//    MongoCredential credential;
//    credential = MongoCredential.createCredential("sampleUser", "myDb",
//            "password".toCharArray());
//      System.out.println("Connected to the database successfully");

            // Accessing the database
            MongoDatabase database = mongo.getDatabase("mydb");

            // Retrieving a collection
            MongoCollection<Document> collection = database.getCollection("property");
            List<Object> output = new ArrayList<>();
            switch(opType){
                case FIND:
                    if (conditions.get(0).values().isEmpty()){
                        output = TransformDataToSimpleProperty(collection.find().limit(limit));
                    }else{
                        output = TransformDataToSimpleProperty(collection.find(conditions.get(0)).limit(limit));
                    }
                    break;
                case GROUP:
                    output = TransformData(collection.aggregate(conditions));
                    break;
            }
            return output;
        }
    }

    private static List<Object> TransformData(MongoIterable iterable){
        Iterator it = iterable.iterator();
        List<Object> output = new ArrayList<>();
        while (it.hasNext()) {
            output.add(it.next());
        }
        return output;
    }

    private static List<Object> TransformDataToSimpleProperty(MongoIterable iterable){
        Iterator it = iterable.iterator();
        List<Object> output = new ArrayList<>();
        while (it.hasNext()) {
            JsonElement jsonElement = gson.toJsonTree(it.next());
            output.add(gson.fromJson(jsonElement, SimpleProperty.class));
        }
        return output;
    }
}
