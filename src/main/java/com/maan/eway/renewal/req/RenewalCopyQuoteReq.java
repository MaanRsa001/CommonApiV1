package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalCopyQuoteReq {
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("RiskId")
	private Integer riskId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("ApplicationId")
	private String applicationId;

	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	
	

	
}
