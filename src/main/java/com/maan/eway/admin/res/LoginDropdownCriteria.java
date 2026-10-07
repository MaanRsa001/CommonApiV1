package com.maan.eway.admin.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDropdownCriteria {

	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("CreatedBy")
	private String createdBy ;
	
	@JsonProperty("EntryDate")
	private Date entryDate ;
	
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy ;
	
	@JsonProperty("AttachedBranches")
	private String attachedBranches;
	
	@JsonProperty("Status")
	private String  status;
	
	@JsonProperty("OaCode")
	private Integer oaCode ;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("BankCode")
	private String bankCode;
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("UserMobile")
	private String userMobile;
	
	@JsonProperty("UserMail")
	private String userMail;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("CustomerName")
	private String customerName;
}
