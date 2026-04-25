package com.hivecloud.gossip.config;

import com.hivecloud.gossip.core.GossipSyncEngine;
import com.hivecloud.gossip.core.NodeDiscovery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;

@Configuration
@EnableConfigurationProperties(GossipProperties.class)
@ConditionalOnProperty(name = "hivecloud.gossip.enabled", havingValue = "true", matchIfMissing = true)
public class GossipAutoConfiguration {

    private final GossipProperties properties;

    public GossipAutoConfiguration(GossipProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestTemplate gossipRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(5000);
            }
        };
        return new RestTemplate(factory);
    }

    @Bean
    public GossipSyncEngine gossipSyncEngine(NodeDiscovery nodeDiscovery, RestTemplate gossipRestTemplate) {
        return new GossipSyncEngine(nodeDiscovery, properties, gossipRestTemplate);
    }
}
