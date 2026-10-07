
package com.maan.eway.admin.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerLoginReq {

	// Login Details
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserTypeDesc")
	private String subUserTypeDesc;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("OaCode")
	private String oaCode;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;

	@JsonProperty("AttachedBranches")
	private List<String> attachedBranches;

	@JsonProperty("AttachedCompanies")
	private List<String> attachedCompanies;

	@JsonProperty("AttachedRegions")
	private List<String> attachedRegions;

	@JsonProperty("Password")
	private String password;
	@JsonProperty("Createdby")
	private String createdBy;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("BrokerCompanyYn")
	private String brokerCompanyYn;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductIds")
	private List<String> productIds;

	@JsonProperty("ReferralIds")
	private List<String> referralIds;

	@JsonProperty("DmlYN")
	private String dmlYN;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("LoginCreationAccess")
	private String LoginAccess;

	@JsonProperty("ReffereralManageAccess")
	private String ReferralAccess;

	@JsonProperty("ReportManageAccess")
	private String ReportAccess;

	@JsonProperty("ClaimCustomerAccess")
	private String ClaimCustomerAccess;

	@JsonProperty("ModuleIds")
	private List<String> moduleIds;
}
