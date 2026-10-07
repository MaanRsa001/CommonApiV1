package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocTypeDropDownReq {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	
}
