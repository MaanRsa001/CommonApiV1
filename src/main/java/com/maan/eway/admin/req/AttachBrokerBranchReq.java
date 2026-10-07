package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachBrokerBranchReq {


	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;
	
	
	@JsonProperty("AttachedBranchCode")
	private String attachedBranch;

	@JsonProperty("AttachedCompany")
	private String attachedCompany;

	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("BranchType")
	private String branchType;
	
	@JsonProperty("Address1")
	private String address1;
	
	@JsonProperty("Address2")
	private String address2;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("Mobile")
	private String mobile;
	
//	@JsonProperty("CoreAppCode")
//	private String coreAppCode;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
//	@JsonProperty("CustomerCode")
//	private String customerCode;
	
//	@JsonProperty("SourceType")
//	private String sourceType;
	
	@JsonProperty("DepartmentCode")
	private String departmentCode;
	
//	@JsonProperty("CustomerCode")
//	private String customerCode;
	
	@JsonProperty("SalePointCode")
	private String salePointCode;
	
}
