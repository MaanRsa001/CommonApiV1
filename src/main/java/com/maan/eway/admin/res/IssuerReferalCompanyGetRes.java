package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerReferalCompanyGetRes {

	@JsonProperty("ReferalId")
	private String referalId ;
	
	@JsonProperty("ReferalName")
	private String referalName ;
	
	@JsonProperty("OldReferalName")
	private String oldReferalName ;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart ;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd ;
	
	@JsonProperty("Status")
	private String status ;
	
}
