package com.maan.eway.admin.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginIssuerGridRes {


	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("OaCode")
	private String oaCode ;

	@JsonProperty("AgencyCode")
	private String agencyCode ;
	
	@JsonFormat(pattern= "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate ;
	
	@JsonProperty("CreatedBy")
	private String createdBy ;
	
	@JsonFormat(pattern= "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate ;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy ;
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("UserMobile")
	private String userMobile ;
	
	@JsonProperty("UserMail")
	private String userMail ;
	
	@JsonProperty("AttachedBranches")
	private List<String> attachedBranches;
	
	@JsonProperty("AttachedProducts")
	private List<String> attachedProducts;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BankCode")
	private String bankCode;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("SubUserTypeId")
	private String subUserTypeId;
	
	
	@JsonProperty("BrokerCompanyYn")
    private String    brokerCompanyYn ;

	@JsonProperty("ReferralIds")
	private List<String> referralIds;
	
}
