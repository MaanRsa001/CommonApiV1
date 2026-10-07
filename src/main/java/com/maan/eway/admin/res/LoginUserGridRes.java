package com.maan.eway.admin.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginUserGridRes {
	

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
	
	@JsonProperty("BranchCodes")
	private String attachedBranches;
	
	@JsonProperty("BankCode")
	private String bankCode;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("BrokerCompanyYn")
    private String    brokerCompanyYn ;
	
	@JsonProperty("CustomerCode")
    private String    customerCode;
	
	@JsonProperty("CustomerName")
    private String    customerName;

}
