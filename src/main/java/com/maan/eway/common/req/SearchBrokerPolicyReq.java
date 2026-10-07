package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchBrokerPolicyReq {

	 @JsonProperty("PolicyNo")
	 private String     policyNo ;
	 
	 @JsonProperty("Limit")
	 private String     limit ;
	 
	 @JsonProperty("Offset")
	 private String     offset ;
	 
	 @JsonProperty("BranchCode")
	 private String     branchCode ;
	 
	 @JsonProperty("InsuraceId")
	 private String     insuranceId ;
	 
	 @JsonProperty("ProductId")
	 private String     productId ;
	 
}
