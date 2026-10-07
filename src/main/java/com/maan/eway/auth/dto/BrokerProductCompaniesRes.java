package com.maan.eway.auth.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerProductCompaniesRes {

	@JsonProperty("InsuranceId")
	private String  insuranceId;  
	@JsonProperty("CompanyName")
	private String  companyName;   
	
	
	@JsonProperty("AttachedProducts")
	private List<BrokerProductsGetRes> attachedProducts ;
}
