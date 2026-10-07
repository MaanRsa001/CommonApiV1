package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OldDocumentsCopyReq {

	@JsonProperty("QuoteNo") 
    private String     quoteNo;
	
	@JsonProperty("ProductId") 
    private String     productId;
	
	@JsonProperty("SectionId") 
    private String     sectionId;
	
	@JsonProperty("InsuranceId") 
    private String     insuranceId;
}
