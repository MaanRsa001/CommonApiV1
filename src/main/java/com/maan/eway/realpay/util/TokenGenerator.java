package com.maan.eway.realpay.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class TokenGenerator {

    @Autowired
    public RestTemplate restTemplate;

    public String getToken(){
        String url = ApiEndPoints.getTokenUrl();
        String client = ApiEndPoints.getClientId() + ":"+ApiEndPoints.getClientSecret();
        String encoded = Base64.getEncoder().encodeToString(client.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic "+encoded);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request,JsonNode.class);
        return response.getBody().get("access_token").asText();
    }
}
