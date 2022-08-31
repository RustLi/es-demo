package com.example.esdata;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by jiangtiteng on 2018-12-19
 */
@Configuration
@Slf4j
public class ElasticSearchConfig {

//    @Value("${elasticsearch.scheme:http}")
    private static final String scheme = "http";//http

    //10.1.80.25
    //10.1.80.42
    //10.1.81.96
//    @Value("${elasticsearch.ip}")
//    private static final String ip = "10.1.80.25,10.1.80.42,10.1.81.96";
    private static final String ip = "localhost";

    //    @Value("${elasticsearch.port:9200}")
    private static final int port = 9200;//9200

//    @Value("${elasticsearch.username:}")
    private static final String username = "";//

//    @Value("${elasticsearch.password:}")
    private static final String password = "";//

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        log.info("init high level es client with ip={}", ip);
        String[] hostArray = ip.split(",");
        for (String host: hostArray) {
            System.out.println("host = " + host);
        }
        HttpHost[] httpHosts = Arrays.stream(hostArray)//
                .map(host -> new HttpHost(host, port, scheme)).toArray(HttpHost[]::new);

        RestClientBuilder builder = RestClient.builder(httpHosts);

        if (StringUtils.isNoneBlank(username, password)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }

        return new RestHighLevelClient(builder);
    }

    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Lazy
    @Bean
    public IndicesAdminClient indicesAdminClient(TransportClient transportClient) {
        return transportClient.admin().indices();
    }

    @Lazy
    @Bean
    public TransportClient transportClient() {
        Settings settings = Settings.builder()
                .put("client.transport.sniff", "false")
                .put("cluster.name", "elasticsearch")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        String[] hostArray = ip.split(",");

        Arrays.stream(hostArray).forEach(host -> {
            try {
                InetAddress inetAddress = InetAddress.getByName(host);
                TransportAddress transportAddress = new TransportAddress(inetAddress, 9300);
                client.addTransportAddress(transportAddress);
            } catch (Exception e) {
                log.error("unknown host,host={}", host, e);
            }
        });
        return client;
    }
}
