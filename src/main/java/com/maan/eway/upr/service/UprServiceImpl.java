package com.maan.eway.upr.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.service.impl.PaymentDbService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.upr.dto.UprApiResponse;
import com.maan.eway.upr.dto.UprPolicyInsertReq;
import com.maan.eway.upr.dto.UprPolicyResponse;



@Service
public class UprServiceImpl implements UprService{
	
	private Logger log = LogManager.getLogger(UprServiceImpl.class);
	

	Gson json = new Gson();
	
	@Value(value = "${UprPolicyInsert}")
	private String UprPolicyInsert;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired private PaymentDbService paymentDbService;
	
	@Override
	public List<UprPolicyResponse> saveUpr(String  policyno) {
		 List<UprPolicyResponse> resList1 = new ArrayList<>();
		try {
			UprPolicyInsertReq req = new UprPolicyInsertReq();
			HomePositionMaster byPolicyNo = paymentDbService.findHomeByPolicyNo(policyno);
			if(byPolicyNo!=null)
			{
				req.setPolicyNo(byPolicyNo.getPolicyNo());
				req.setLobCode(String.valueOf(byPolicyNo.getProductId()));
				req.setLobname(byPolicyNo.getProductName());
				req.setCommissionAmount(byPolicyNo.getCommission());
				req.setGrossPremium(byPolicyNo.getOverallPremiumLc());
				req.setNetPremium(byPolicyNo.getPremiumLc());
				req.setPolicyterm(byPolicyNo.getPolicyTerm());
				Date inceptionDate = byPolicyNo.getInceptionDate();
				if (inceptionDate != null) {
					LocalDate inceptionDate1 = inceptionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					req.setInceptionDate(inceptionDate1);
				}
				req.setInsuredName(byPolicyNo.getCustomerName());
				LocalDate expdate = byPolicyNo.getExpiryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				req.setExpiryDate(expdate);

			
			
			
			String url = UprPolicyInsert ;
		//	String auth = ClaimBasicAuthName +":"+ ClaimBasicAuthPass;
			
	    //    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")) );
	    //    String authHeader = "Basic " + new String( encodedAuth );
	     	RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
		//	headers.set("Authorization",authHeader);
			HttpEntity<Object> entityReq = new HttpEntity<Object>(req, headers);
	
			log.info("Api Url -----------> " +  url );
		//    log.info("Request -----------> " + json.toJson(req) );
			ResponseEntity<Object> response = restTemplate.postForEntity(url, entityReq, Object.class);
			 
			
			if (response != null && response.getBody() != null) {

			    // Create ObjectMapper and register JavaTimeModule
			    ObjectMapper mapper = new ObjectMapper();
			    mapper.registerModule(new JavaTimeModule()); // <-- handles LocalDate and LocalDateTime
			    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // optional

			    // Map to wrapper class first
			    UprApiResponse apiResponse = mapper.convertValue(response.getBody(), UprApiResponse.class);

			    // If you want a list (even for single object)
			   
			    if (apiResponse.getData() != null) {
			        resList1.add(apiResponse.getData());
			    }

			    // Logging
			    log.info("Response List -----------> " + resList1);
			}
			}
			
		}catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
		return resList1;
	}

}
