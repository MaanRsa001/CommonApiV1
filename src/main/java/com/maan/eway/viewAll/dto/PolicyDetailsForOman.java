package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyDetailsForOman {
	
	@JsonProperty("CertificateNo")
	private String policyNo;
	
	@JsonProperty("DateIfIssued")
	private String inceptionDate;
	
	@JsonProperty("PolicyFromDate")
	private String policyFromDate;
	
	@JsonProperty("PolicyToDate")
	private String policyToDate;
	
	@JsonProperty("EffectiveDate")
	private String effectiveDate;
	
	

}
