package web.social.facebook.common;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticClient {
    @Value("${elastic.host}")
    private String host;

    @Value("${elastic.cluster.name}")
    private String clusterName;

    @Bean
    public Client createClient() throws UnknownHostException {
        Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
        return TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
    }
}
