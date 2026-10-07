package com.maan.eway.excelupload.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdditionalInfoRequest {

	
	@JsonProperty("QuoteNo")
    private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
    private String requestReferenceNo;

	@JsonProperty("EndtTypeId")
    private String endtTypeId;
	
	@JsonProperty("PolicyNo")
    private String policyNo;

}

