package com.maan.eway.reinsurance.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReInsuranceQuoteReq {
	
    @JsonProperty("QuoteNo")
    private String quoteNo;
    
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
    
    @JsonProperty("CreatedBy")
    private String createdBy;
    
    @JsonProperty("PolicyNo")
    private String policyNo;
    
    @JsonProperty("UserType")
    private String userType;

}
