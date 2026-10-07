package com.maan.eway.reinsurance.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RiGridReq {
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	@JsonProperty("InsuranceId")
	private String companyid;
	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("LoginId")
	private String loginId;
	@JsonProperty("ApplicationId")
	private String applicationId;

	@JsonProperty("SourceType")
	private String sourceType;

	@JsonProperty("BdmCode")
	private String bdmCode;
	@JsonProperty("UserType")
	private String userType;
	@JsonProperty("SubUserType")
	private String subUserType;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("Limit")
	private String limit;
	@JsonProperty("Offset")
	private String offset;

	@JsonProperty("Type")
	private String type;

	@JsonProperty("LeadSeqNo")
	private String leadSeqNo;
}
