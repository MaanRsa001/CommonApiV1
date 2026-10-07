package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetExistingBrokerListReq {

	@JsonProperty("ProductId")
    private String    productId    ;
	
	@JsonProperty("InsuranceId")
    private String     companyId    ;
	
	@JsonProperty("LoginId")
    private String     loginId    ;
	
	@JsonProperty("Status")
    private String    status    ;
	
	@JsonProperty("BranchCode")
    private String    branchCode    ;
}
