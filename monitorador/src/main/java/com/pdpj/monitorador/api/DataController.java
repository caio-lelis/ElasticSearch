package com.pdpj.monitorador.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private RestHighLevelClient elasticsearchClient;

   @PostMapping("/store")
public ResponseEntity<String> storeData(@RequestParam String indexName, @RequestBody String data) {
    try {
        IndexRequest request = new IndexRequest(indexName);
        request.source(Collections.singletonMap("data", data), XContentType.JSON);
        IndexResponse response = elasticsearchClient.index(request, RequestOptions.DEFAULT);
        
        String documentId = response.getId();

        return ResponseEntity.ok("Data stored successfully with ID: " + documentId);
    } catch (IOException e) {
        System.out.println(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error storing data");
    }
}

@GetMapping("/retrieve/{indexName}/{id}")
public ResponseEntity<String> retrieveData(@PathVariable String indexName, @PathVariable String id) {
    try {
        GetRequest getRequest = new GetRequest(indexName, id);
        GetResponse getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            return ResponseEntity.ok(sourceAsString);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (IOException e) {
        System.out.println(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving data");
    }
}

@GetMapping("/retrieve-all/{indexName}")
public ResponseEntity<String> retrieveAllData(@PathVariable String indexName) {
    try {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();

        List<String> results = new ArrayList<>();
        for (SearchHit hit : hits) {
            results.add(hit.getSourceAsString());
        }

        return ResponseEntity.ok(results.toString());
    } catch (IOException e) {
        System.out.println(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving data");
    }
}


}