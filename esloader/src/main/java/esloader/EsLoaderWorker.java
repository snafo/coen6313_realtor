package esloader;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

public class EsLoaderWorker {
    private List<Map<String, Object>> documents;

    private Integer cmsDbId;

    private String clusterName = "";
    private String index = "";
    private String type = "";

    private String esKeyColField = "propertyId";

    private final Logger logger = LoggerFactory.getLogger(EsLoaderWorker.class);

    public EsLoaderWorker(List<Map<String, Object>> documents) throws Exception {
        this.documents = documents;

        clusterName = "staging-es";
        index = "test";
        type = "property";

    }

    public Integer persisit() {
        int tatalLoaded = 0;
        Client client = null;
        try {
            client = ESClient.getClient();
            tatalLoaded = bulkLoading(client, documents);
        } catch(Exception e) {
            logger.error("Got exception " + e.getMessage() + " while loading to elasticsearch ...");
            e.printStackTrace();
            throw e;
        }

        if(client != null) {
            client.close();
            client = null;
        }

        return tatalLoaded;
    }

    private int bulkLoading(Client client, List<Map<String, Object>> documents) {
        int failedCount = 0;

        if(client != null && documents != null && documents.size() > 0) {
            BulkRequestBuilder bulkRequest = client.prepareBulk();

            for(Map<String, Object> item : documents) {
                if(item != null && item.size() > 0) {
                    try {
                        String esId = getEsId(item);
                        bulkRequest.add(client.prepareUpdate(index, type, esId).setDoc(item).setUpsert(item));
                    }
                    catch(Exception e) {
                        logger.error("got exception while adding document: {}", item);
                        e.printStackTrace();
                    }
                }
            }

            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if(bulkResponse.hasFailures()) {
                for(BulkItemResponse response : bulkResponse.getItems()) {
                    if(response.isFailed()) {
                        logger.error("Failed: " + response.getFailureMessage());
                        failedCount++;
                    }
                }
            } else {
                logger.info("{} data successfully loaded to elasticsearch ...", (documents.size() - failedCount));
            }
        }

        return documents.size() - failedCount;
    }

    private String getEsId(Map<String, Object> item) {
        return item.get("propertyId").toString();
    }
}
