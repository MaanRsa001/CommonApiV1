package com.maan.eway.realpay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service

public class TokenGeneratorForEway {

    @Value(value = "${finace.generateToken}")
    private String generateToken;

    public String getToken(){
        try{
            RestTemplate restTemplate = new RestTemplate();
            String loginPayload = "{\"username\": \"admin\", \"password\": \"admin\"}";
            HttpHeaders loginHeaders = new HttpHeaders();
            loginHeaders.setContentType(MediaType.APPLICATION_JSON);
            loginHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> loginRequest = new HttpEntity<>(loginPayload, loginHeaders);
            ResponseEntity<String> loginResponse = restTemplate.postForEntity(generateToken, loginRequest,
                    String.class);
            String token = loginResponse.getBody();
            System.out.println("Token: " + token);
            return token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
