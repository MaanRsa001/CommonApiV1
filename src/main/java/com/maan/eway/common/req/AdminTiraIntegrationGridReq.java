package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminTiraIntegrationGridReq {

	   @JsonProperty("QuoteNo")
	   private String     quoteNo     ;
	   @JsonProperty("InsuranceId")
	   private String     insuranceId     ;
	   @JsonProperty("ProductId")
	   private String     productId     ;
	   @JsonProperty("BranchCode")
	   private String     branchCode     ;
	   @JsonProperty("StartDate")
	   private String     pushStartDate     ;
	   @JsonProperty("EndDate")
	   private String     pushEndDate ;
}
