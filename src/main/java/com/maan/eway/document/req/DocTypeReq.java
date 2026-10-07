package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocTypeReq {


	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("SectionId")
	private String sectionId ;
	
	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("UserType")
	private String userType ;
	
	
}
