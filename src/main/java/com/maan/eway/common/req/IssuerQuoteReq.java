package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerQuoteReq {


	   @JsonProperty("BranchCode")
	   private String     branchCode ;
	   @JsonProperty("InsuranceId")
	   private String    insuranceId ;
	   @JsonProperty("ApplicationId")
	   private String     applicationId;
	   @JsonProperty("LoginId")
	   private String     loginId;
	   @JsonProperty("ProductId")
	   private String     productId     ;
	   @JsonProperty("Status")
	   private String     status     ;
	
}
