package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachCompnayProductRequest {

	@JsonProperty("LoginId")
	private String loginId ;
	@JsonProperty("InsuranceId")
	private String  insuranceId; 
	
	@JsonProperty("CreatedBy")
	private String  createdBy; 
	
	
	@JsonProperty("ProductIds")
	private List<String> productIds ;
	
	 @JsonProperty("ReferralIds")
     private List<String>  referralIds ;

	
}
