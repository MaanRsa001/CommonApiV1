package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubCoverGetAllReq {


	@JsonProperty("CoverId")
    private String coverId; 
	
	@JsonProperty("SubCoverId")
    private String subCoverId; 
	
	@JsonProperty("ProductId")
    private String productId; 
	
	@JsonProperty("SectionId")
    private String sectionId; 

	@JsonProperty("InsuranceId")
    private String insuranceId;
	
	@JsonProperty("Limit")
    private String limit;
	
	@JsonProperty("Offset")
    private String offset;
}
