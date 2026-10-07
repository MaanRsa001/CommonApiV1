package com.maan.eway.realpay.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestApiCaller {
    
    private static final Logger logger = LoggerFactory.getLogger(RestApiCaller.class);
    
    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public TokenGenerator tokenGenerator;

    public ResponseEntity<String> callGetExternal(String url) {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenGenerator.getToken();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }
    
    public ResponseEntity<String> callGetExternalNew(String url) {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenGenerator.getToken();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<JsonNode> callPostExternal(String url, HttpEntity request){
        return restTemplate.postForEntity(url, request, JsonNode.class);
    }

    public ResponseEntity<JsonNode> callPutExternal(String url, HttpEntity request){
        return restTemplate.exchange(url, HttpMethod.PUT, request, JsonNode.class);
    }
    
    /**
     * DELETE call with empty JSON body for RealPay compliance
     */
    public ResponseEntity<JsonNode> callDeleteExternal(String url, HttpEntity<String> request) {
        logger.debug("Executing DELETE request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.DELETE, request, JsonNode.class);
    }

    /**
     * DELETE call with automatic empty JSON body
     */
    public ResponseEntity<JsonNode> callDeleteExternal(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = tokenGenerator.getToken();
        headers.set("Authorization", "Bearer " + token);
        
        // RealPay requires empty JSON body for DELETE
        String emptyJsonBody = "{}";
        HttpEntity<String> entity = new HttpEntity<>(emptyJsonBody, headers);
        
        logger.debug("Executing DELETE request with empty body to: {}", url);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, JsonNode.class);
    }
}
