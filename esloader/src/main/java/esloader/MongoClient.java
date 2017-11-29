package esloader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MongoClient {
    private static Gson gson = new Gson();

    public static List<Map<String,Object>> provideData(){
        // Creating a Mongo client
        try (com.mongodb.MongoClient mongo = new com.mongodb.MongoClient("localhost", 27017)) {
            // Accessing the database
            MongoDatabase database = mongo.getDatabase("mydb");

            // Retrieving a collection
            MongoCollection<Document> collection = database.getCollection("property");
            List<Map<String,Object>> output  = TransformDataToEsEntity(collection.find());
            return output;
        }
    }

    private static List<Map<String,Object>> TransformDataToEsEntity(MongoIterable iterable){
        Iterator it = iterable.iterator();
        List<Map<String,Object>> output = new ArrayList<>();
        while (it.hasNext()) {
            JsonElement jsonElement = gson.toJsonTree(it.next());
            output.add(transformTOMap(gson.fromJson(jsonElement, EsEntity.class)));
        }
        return output;
    }

    private static Map<String,Object> transformTOMap(EsEntity esEntity){
//        String description = esEntity.getDescription() + " " +
//                esEntity.getType() + " " +
//                esEntity.getAddress() + " " +
//                esEntity.getNeighbourhood() + " " +
//                esEntity.getSublocality() + " " +
//                esEntity.getLocality();

        Map<String, Object> output = new HashMap<>();
        output.put("propertyId", esEntity.getPropertyId());
        output.put("discription", esEntity.getDiscription());
        output.put("location", esEntity.getLocation());
        output.put("price", esEntity.getPrice());
        output.put("area", esEntity.getArea());
        return output;
    }
}
