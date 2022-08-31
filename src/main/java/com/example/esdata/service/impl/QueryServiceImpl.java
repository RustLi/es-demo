package com.example.esdata.service.impl;

import com.example.esdata.service.QueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class QueryServiceImpl implements QueryService {

    public static final String INDEX = "msm-log-*";
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void getAndOut() {
        System.out.println(111);
        searchFile();
    }

    private List<String> searchFile() {
        System.out.println(423424);
        SearchRequest searchRequest = new SearchRequest(INDEX);
        searchRequest.types("doc");
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("unique_id","0530235844147289");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            System.out.println("search es failed: e = " + e);
            System.out.println("Search es failed, searchRequest={}" + searchRequest);
            searchResponse = null;
        }
        if (Objects.isNull(searchResponse)) {
            return Collections.emptyList();
        }
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        List<String> ids = Lists.newArrayList();
        for (SearchHit searchHit : searchHits) {
            ids.add(searchHit.getId());
        }
        return ids;
    }


    @Override
    public void queryTest() {
        log.info("queryTest: start 222");

        //手动创建
//        RestHighLevelClient restHighLevelClient = getClient();

        SearchRequest searchRequest = new SearchRequest("customer");
        searchRequest.types("doc");
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("_id", "1");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;

        log.info("queryTest: searchRequest={}", searchRequest);
        try {
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            System.out.println("search es failed: e = " + e);
        }
        log.info("queryTest: searchResponse={}", searchResponse);
        if (searchResponse == null){
            return;
        }
        log.info("queryTest: end");
    }

    public static RestHighLevelClient getClient(){
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }
}
