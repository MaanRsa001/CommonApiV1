package com.maan.eway.crm.bean;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuoteReq {
	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("LeadId")
	private Long leadId;

	@JsonProperty("QuoteStatus")
	private String quoteStatus;
	

	@JsonProperty("InsuranceId")
	private String insuranceId;
	
}
