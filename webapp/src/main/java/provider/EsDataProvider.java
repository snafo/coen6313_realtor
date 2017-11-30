package provider;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by qinyu on 2017-11-29.
 */
public class EsDataProvider {
    public static List<String> provideData(String words) throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("discription", words).minimumShouldMatch("90%"));
        SearchResponse searchResponse = EsClient
                .getClient()
                .prepareSearch("test")
                .setTypes("property")
                .setQuery(boolQuery)
                .setSize(1000)
                .execute()
                .actionGet();

        List<String> propertyIds = new ArrayList<>();
        if (searchResponse != null && searchResponse.getHits().getTotalHits() > 0) {
            SearchHits hits = searchResponse.getHits();
            for (int i = 0; i < hits.getTotalHits(); i++){
                System.out.println(i);
                SearchHit hit = hits.getAt(i);
                Map<String, Object> esMap = hit.getSourceAsMap();
                propertyIds.add((String)esMap.get("propertyId"));
            }
        }
        return propertyIds;
    }


    public static ListenableActionFuture<SearchResponse> executeQuery(String words) throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("discription", words).minimumShouldMatch("100%"));

        ListenableActionFuture<SearchResponse> future =  EsClient
                .getClient()
                .prepareSearch("test")
                .setTypes("property")
                .setQuery(boolQuery)
                .setSize(1000)
                .execute();

        return future;
    }

    public static List<String> getData(ListenableActionFuture<SearchResponse> future){
        SearchResponse searchResponse = future.actionGet();

        List<String> propertyIds = new ArrayList<>();
        if (searchResponse != null && searchResponse.getHits().getTotalHits() > 0) {
            SearchHits hits = searchResponse.getHits();
            for (int i = 0; i < hits.getTotalHits(); i++){
                System.out.println(i);
                SearchHit hit = hits.getAt(i);
                Map<String, Object> esMap = hit.getSourceAsMap();
                propertyIds.add((String)esMap.get("propertyId"));
            }
        }
        return propertyIds;
    }
}
