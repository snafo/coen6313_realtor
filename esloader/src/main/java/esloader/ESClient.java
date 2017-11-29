package esloader;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;


public class ESClient {
    private static String clusterName = "realtor";
    private static String nodeName = "node_1";


    private static final Logger logger = LoggerFactory.getLogger(ESClient.class);

    public static Client getClient() {
        TransportClient transportClient;

        try {
            Settings settings = Settings.builder().
                    put("cluster.name", clusterName).
                    put("node.name", nodeName).
                    put("client.transport.sniff", true).
                    put("client.transport.ping_timeout","60s").build();

            transportClient = new PreBuiltTransportClient(settings);
            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("DEV-CSDR-001.dev.mgcorp.co"), 9300));
//            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("DEV-CSDR-002.dev.mgcorp.co"), 9300));
//            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("DEV-CSDR-003.dev.mgcorp.co"), 9300));
        }
        catch (Exception e) {
            logger.error("Error while getting ES client: {}", e.getMessage());
            e.printStackTrace();
            transportClient = null;
        }

        return (Client) transportClient;
    }

}
