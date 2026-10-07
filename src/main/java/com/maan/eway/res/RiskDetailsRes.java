package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RiskDetailsRes {

	@JsonProperty("ProductId")
    private String     productId ;
	
	@JsonProperty("ProductName")
    private String     productName ;
	
	@JsonProperty("CustomerId")
    private String     customerId ;
	
	@JsonProperty("CustomerRequestReferenceNo")
    private String     requestReferenceNo ;
	
	@JsonProperty("InsuranceId")
    private String     insuranceId ;
	
	@JsonProperty("BranchCode")
    private String     branchCode;
	
	@JsonProperty("CreatedBy")
    private String     createdBy;
	
	@JsonProperty("RiskCount")
    private String     riskCount;
	
	

}
