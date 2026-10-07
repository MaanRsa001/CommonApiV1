package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetBrokerListDropDownReq {
	
	@JsonProperty("UserType")
	private String userType ;
	
	@JsonProperty("SubUserType")
	private String subUserType ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BranchCode")
	private String branchCode;

}
