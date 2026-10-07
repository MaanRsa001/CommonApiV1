package com.maan.eway.common.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocValidationReq {

	@JsonProperty("RiskId")
	private String riskId ;
	
	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("SectionId")
	private String sectionId ;
	
	@JsonProperty("DocDesc")
	private String docDesc ;
	
	@JsonProperty("ProductDesc")
	private String productDesc ;
	
	@JsonProperty("SectionDesc")
	private String sectionDesc ;
	
}
