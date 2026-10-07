package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class NonMotorBrokerRes {

	@JsonProperty("ApplicationId")
	private String applicationId;
	@JsonProperty("BrokerCode")
	private String brokerCode;
	@JsonProperty("SubUsertype")
	private String subUserType;
	@JsonProperty("LoginId")
	private String loginId;
	@JsonProperty("AgencyCode")
	private String agencyCode;
	@JsonProperty("UserType")
	private String userType;
	@JsonProperty("BankCode")
	private String bankCode;
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	@JsonProperty("SourceTypeId")
	private String sourceTypeId;
	@JsonProperty("SourceType")
	private String sourceType;
	@JsonProperty("CustomerCode")
	private String customerCode;
	@JsonProperty("BdmCode")
	private String bdmCode;
	@JsonProperty("CommissionType")
	private String commissionType;

}
