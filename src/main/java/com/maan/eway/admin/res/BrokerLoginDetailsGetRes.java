package com.maan.eway.admin.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerLoginDetailsGetRes {

	// Login Details
	@JsonProperty("LoginId")
    private String     loginId      ;
	@JsonProperty("UserType")
    private String     userType     ;
	@JsonProperty("SubUserType")
    private String     subUserType  ;
	@JsonProperty("OaCode")
    private String     oaCode       ;

	@JsonProperty("AgencyCode")
	private String agencyCode ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("AttachedBranches")
    private List<String>     getBranches ;
	
	@JsonProperty("ProductIds")
    private List<String>     productIds ;
	
	/*	@JsonProperty("AttachedCompanies")
    private List<String>     getCompanies ;
	
	@JsonProperty("AttachedRegions")
    private List<String>     getRegions ; 
	
	@JsonProperty("Password")
    private String     password     ; */
	@JsonProperty("Createdby")
    private String     createdBy    ;
	@JsonProperty("Status")
    private String     status       ;
	@JsonProperty("BankCode")
    private String     bankCode    ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date     entryDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
    private Date     updatedDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedBy")
    private String     updatedBy;
	
	@JsonProperty("BrokerCompanyYn")
    private String    brokerCompanyYn ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart ;
	    
}
