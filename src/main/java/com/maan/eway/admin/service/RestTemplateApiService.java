package com.maan.eway.admin.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.admin.req.PolicyTypeMasterGetReq;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.master.req.SectionCoverMasterSaveReq;
import com.maan.eway.req.calcengine.CalcEngine;



@Service
public class RestTemplateApiService {

	  private final RestTemplate restTemplate;

	    public RestTemplateApiService(RestTemplate restTemplate) {
	        this.restTemplate = restTemplate;
	    }
	    

	    public DropdownCommonRes callSecondApi(String url, PolicyTypeMasterGetReq policyTypeMasterGetReq,  String token) {
	       

	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + token); 
	        headers.setContentType(MediaType.APPLICATION_JSON); 

	        HttpEntity<PolicyTypeMasterGetReq> requestEntity = new HttpEntity<>(policyTypeMasterGetReq, headers);
	        ResponseEntity<DropdownCommonRes> responseEntity = 
	                restTemplate.postForEntity(url, requestEntity, DropdownCommonRes.class);
	        if (responseEntity.getStatusCode().is2xxSuccessful()) {
	            return responseEntity.getBody(); 
	        } else {
	            return null; 
	        }
	    }
	    
	    public CommonRes callcoverinsertapi(String url, List<SectionCoverMasterSaveReq> reqlist,  String token) {
		       

	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + token); 
	        headers.setContentType(MediaType.APPLICATION_JSON); 

	        HttpEntity<List<SectionCoverMasterSaveReq>> requestEntity = new HttpEntity<>(reqlist, headers);
	        ResponseEntity<CommonRes> responseEntity = 
	                restTemplate.postForEntity(url, requestEntity, CommonRes.class);
	        if (responseEntity.getStatusCode().is2xxSuccessful()) {
	            return responseEntity.getBody(); 
	        } else {
	            return null; 
	        }
	    }
	    
	    public EserviceMotorDetailsSaveRes callEngine(String Url ,CalcEngine engine, String token) {
		    String url = Url;
		    HttpHeaders headers = new HttpHeaders();
		    headers.set("Authorization", "Bearer " + token); 
		    headers.setContentType(MediaType.APPLICATION_JSON); 

		    try {
		        HttpEntity<CalcEngine> requestEntity = new HttpEntity<>(engine, headers);
		        ResponseEntity<EserviceMotorDetailsSaveRes> responseEntity = 
		        		restTemplate.postForEntity(url, requestEntity, EserviceMotorDetailsSaveRes.class);

		        if (responseEntity.getStatusCode().is2xxSuccessful()) {
		            return responseEntity.getBody(); 
		        } else {
		            throw new RuntimeException("Request failed with status code: " + responseEntity.getStatusCode());
		        }
		    } catch (Exception ex) {
		        throw new RuntimeException("HTTP error occurred: " + ex.getMessage(), ex);
		    }
		}
	  
	    
}
