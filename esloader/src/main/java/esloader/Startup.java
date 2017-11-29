package esloader;

import java.util.List;
import java.util.Map;

/**
 * Created by qinyu on 2017-11-28.
 */
public class Startup {
    public static void main(String[] args){
        try {
            List<Map<String,Object>> documents = MongoClient.provideData();
            EsLoaderWorker esLoaderWorker = new EsLoaderWorker(documents);
            esLoaderWorker.persisit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
