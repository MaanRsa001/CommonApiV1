package com.maan.eway.realpay.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maan.eway.realpay.util.ApiEndPoints;
import com.maan.eway.realpay.util.RestApiCaller;
import com.maan.eway.realpay.util.TokenGenerator;

@Service
public class MandateService {
    @Autowired
    public RestApiCaller restApiCaller;

    @Value("${client.merchant}")
    private String merchant;

    @Autowired
    public TokenGenerator tokenGenerator;

    @Autowired
    private ClientService clientService; 

    public JsonNode callMandate(String product, JsonNode requestWrapper, String method) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.mandates+"/"+product+"?Version=v1&BeneficiaryUser="+merchantId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = tokenGenerator.getToken();
        headers.set("Authorization", "Bearer "+token);

        Map<String,JsonNode> requestData = new HashMap<>();
        if(method.equals("put")){
            requestData.put("MandatePutRequest",requestWrapper);
            HttpEntity<Map> requestEntity = new HttpEntity<>(requestData, headers);
            return restApiCaller.callPutExternal(url,requestEntity).getBody();
        }else {
            requestData.put("MandatePostRequest",requestWrapper);
            HttpEntity<Map> requestEntity = new HttpEntity<>(requestData, headers);
            return restApiCaller.callPostExternal(url,requestEntity).getBody();
        }
    }

    public String getMandates(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.mandates+"/"+product+"?Version=v1&BeneficiaryUser="+merchantId;
        return restApiCaller.callGetExternal(url).getBody();
    }
}