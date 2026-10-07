package com.maan.eway.admin.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"SubUserTypeDesc"})
public class CommonLoginInformationReq {

	// Login Details
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserTypeDesc")
	private String subUserTypeDesc;
	
	@JsonProperty("SubUserTypeId")
	private String subUserTypeId;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("Oacode")
	private String oaCode;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("AttachedBranches")
	private List<String> attachedBranches;

	@JsonProperty("AttachedCompanies")
	private List<String> attachedCompanies;

	@JsonProperty("AttachedRegions")
	private List<String> attachedRegions;

	@JsonProperty("MenuId")
	private List<String> menuId;

	@JsonProperty("Password")
	private String password;
	@JsonProperty("Createdby")
	private String createdBy;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("BankCode")
	private String bankCode;

	@JsonProperty("BrokerCompanyYn")
	private String brokerCompanyYn;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	// Deposit Cbc Request
	@JsonProperty("CbcNo")
	private String cbcNo;

	@JsonProperty("DmlYN")
	private String dmlYN;

	@JsonProperty("LoginCreationAccess")
	private String LoginAccess;

	@JsonProperty("ReffereralManageAccess")
	private String ReferralAccess;

	@JsonProperty("ReportManageAccess")
	private String ReportAccess;

	@JsonProperty("ClaimCustomerAccess")
	private String ClaimCustomerAccess;
	
	@JsonProperty("ModuleIds")
	private List<String> moduleIds ;

}
