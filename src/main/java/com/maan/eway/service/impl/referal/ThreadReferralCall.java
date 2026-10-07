package com.maan.eway.service.impl.referal;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.common.res.CommonDropdown;
import com.maan.eway.req.referal.ReferralRequest;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.referal.MasterReferal;

public class ThreadReferralCall implements Callable<Object> {
	
	private ReferralRequest request;
	
	public ThreadReferralCall(ReferralRequest request) {
		super();
		this.request = request;
	}

	@Override
	public MasterReferal call() throws Exception {
		MasterReferal referal=null;
		
		ResponseEntity<CommonDropdown> postForEntity =null;
		try {
			{
				RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
						
						
				HttpHeaders header=new HttpHeaders();
				header.setContentType(MediaType.APPLICATION_JSON);
				//header.setCharset("UTF-8");
				header.setBearerAuth(request.getTokenl());
				 


				HttpEntity<?> requestent = 
						new HttpEntity<>(this.request.getApiRequest(), header);

				System.out.println( new Date()+" Start "+ request.getApiLink());
				postForEntity = temp.exchange(this.request.getApiLink(),HttpMethod.POST, requestent, new ParameterizedTypeReference<CommonDropdown>() {} );
				System.out.println( new Date()+" End "+ request.getApiLink());

			}

			if(postForEntity.getStatusCode().is2xxSuccessful()) {
				DropDownRes[] commonResponse = postForEntity.getBody().getCommonResponse();
				List<DropDownRes> response=Arrays.asList(commonResponse);
				if(response!=null && !response.isEmpty()) {
					List<DropDownRes> collect = response.stream().filter(t-> (t.getStatus().equals("R") && t.getCode().equals(request.getPrimaryId()))).collect(Collectors.toList());
					if(collect!=null && !collect.isEmpty()) {
						referal=MasterReferal.builder().isreferral(true).referralDesc("Master Referral for "+collect.get(0).getCodeDesc()).build();
					}else
						referal=MasterReferal.builder().isreferral(false).build();
				}else {
					referal=MasterReferal.builder().isreferral(true).referralDesc("No response from api"+" "+ request.getApiLink()+", "+request.getApiRequest()).build();
				}
			}else {
				referal=MasterReferal.builder().isreferral(true).referralDesc("Api is Not Up: Link "+ request.getApiLink()).build();
			}			
		}catch (Exception e) {
			e.printStackTrace();
			referal=MasterReferal.builder().isreferral(true).referralDesc("Exception: In referral "+e.getMessage()+" "+ request.getApiLink()+", "+request.getApiRequest()).build();					
		}finally {
			
		}
		return referal;
	}

}
