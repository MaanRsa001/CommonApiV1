package com.maan.eway.renewal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetBrokerListRes {


	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("Status")
	private String  status;
	
	@JsonProperty("OaCode")
	private Integer oaCode ;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
}
