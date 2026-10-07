package com.maan.eway.admin.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetBrokerBranchRes {


	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	
	
	@JsonProperty("AttachedBranchCode")
	private String attachedBranch;
	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;

	@JsonProperty("AttachedCompany")
	private String brokerAttachedCompany;

	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("Address1")
	private String address1;
	
	@JsonProperty("Address2")
	private String address2;
	
	
	@JsonProperty("BranchType")
	private String branchType;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("Mobile")
	private String mobile;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("Status")
	private String status;
	
//	@JsonProperty("SourceType")
//	private String sourceType;
	
	@JsonProperty("DepartmentCode")
	private String departmentCode;

	
//	@JsonProperty("CustomerCode")
//	private String customerCode;
	

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("SalePointCode")
	private String salePointCode;
}
