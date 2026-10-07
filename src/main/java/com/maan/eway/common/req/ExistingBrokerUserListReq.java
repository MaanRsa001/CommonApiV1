package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExistingBrokerUserListReq {
	
	@JsonProperty("ProductId")
    private String    productId    ;
	
	@JsonProperty("InsuranceId")
    private String     companyId    ;
	
	@JsonProperty("LoginId")
    private String     loginId    ;
	
	@JsonProperty("Status")
    private String    status    ;
	@JsonProperty("ApplicationId")
    private String     applicationId    ;
	@JsonProperty("UserType")
    private String    userType    ;
	
	@JsonProperty("BranchCode")
    private String    branchCode    ;
	@JsonProperty("Type")
    private String    type    ;

}
