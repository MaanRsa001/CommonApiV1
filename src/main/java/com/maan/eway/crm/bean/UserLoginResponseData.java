package com.maan.eway.crm.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserLoginResponseData {

	@JsonProperty("ValidateToken")
	private boolean validateToken;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("UserType")
	private String userType;

	@JsonProperty("SubUserType")
	private String subUserType;

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("MenuAccessYN")
	private String menuAccessYN;

	@JsonProperty("ReferalAccessYN")
	private String referalAccessYN;

	@JsonProperty("ReportAccessYN")
	private String reportAccessYN;

	@JsonProperty("LoginUserCoreAppCode")
	private String loginUserCoreAppCode;
	@JsonProperty("AttachedBranchDetails")
	private Map<String, AttachedBranchRes> attachedBranchDetails;
}
