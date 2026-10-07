package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginBranchDetailsRes {
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("BelongingBranch")
	private String belongingBranch ;
	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode ;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName ;
	
	
	@JsonProperty("BranchName")
	private String branchName ;
	
	@JsonProperty("RegionCode")
	private String regionCode ;
	
	@JsonProperty("RegionName")
	private String regionName ;
	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	
	@JsonProperty("CompanyName")
	private String companyName ;
	
	@JsonProperty("CompanyLogo")
	private String companyLogo ;
	
	@JsonProperty("AttachedBranchCode")
	private String attachedBranchCode ;
	
	@JsonProperty("AttachedBranchName")
	private String attachedBranchName ;
	
	@JsonProperty("AttachedRegionCode")
	private String attachedRegionCode ;
	
	@JsonProperty("AttachedRegionName")
	private String attachedRegionName ;
	
	@JsonProperty("AttachedCompanyId")
	private String attachedCompanyId ;
	
	
	@JsonProperty("AttachedCompanyName")
	private String attachedCompanyName ;
	
	@JsonProperty("AttachedCompanyLogo")
	private String attachedCompanyLogo ;
	
	@JsonProperty("CurrencyId")
	private String currencyId;
	
	@JsonProperty("SourceType")
	private String sourceType;
	
	@JsonProperty("DepartmentCode")
	private String departmentCode;
	
	@JsonProperty("BrokerBranchNameLocal")
	private String brokerBranchNameLocal ;
	
//	@JsonProperty("CustomerCode")
//	private String customerCode;
//	
//	@JsonProperty("CustomerName")
//	private String customerName ;
	
	@JsonProperty("OriginationCountryId")
	private String originationCountryId ;
	
	@JsonProperty("DestinationCountryId")
	private String destinationCountryId ;
	
	@JsonProperty("DivisionCode")
	private String divisionCode ;
	
}
