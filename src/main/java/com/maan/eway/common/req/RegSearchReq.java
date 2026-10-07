package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegSearchReq {

	  @JsonProperty("BranchCode")
	   private String     branchCode ;
	   @JsonProperty("BrokerBranchCode")
	   private String     brokerBranchCode ;
	   @JsonProperty("InsuranceId")
	   private String     insuranceId     ;
	   
	   @JsonProperty("ProductId")
	   private String     productId ;
	   
	   @JsonProperty("CreatedBy")
	   private String     createdBy;
	   
	   @JsonProperty("Limit")
	   private String     limit;
	   
	   @JsonProperty("Offset")
	   private String     offset;
	   
	   @JsonProperty("RegisterNumber")
	   private String     registerNumber;
	   
}
