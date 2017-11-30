package provider;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

/**
 * Created by qinyu on 2017-11-29.
 */
public class EsClient {
    private static Client client;

    public static Client getClient() {
        if (client == null){
            initClient();
        }
        return client;
    }

    private static void initClient(){
        TransportClient transportClient;

        try {
            Settings settings = Settings.builder().
                    put("cluster.name", "realtor").
                    put("client.transport.sniff", true).
                    put("client.transport.ping_timeout","60s").build();

            transportClient = new PreBuiltTransportClient(settings);
            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        }
        catch (Exception e) {
            e.printStackTrace();
            transportClient = null;
        }

        client = (Client) transportClient;
    }
}
