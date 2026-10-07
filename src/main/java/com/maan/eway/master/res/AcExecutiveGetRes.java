package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveGetRes {

	@JsonProperty("AcExecutiveId")
	private String acExecutiveId;
	
	@JsonProperty("AcExecutiveName")
	private String acExecutiveName;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;	
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("BankCode")
	private String bankCode;
	@JsonProperty("BankName")
	private String bankName;
	

	@JsonProperty("OaCode")
	private String oaCode;
	
	@JsonProperty("ExistCustCommPercent")
	private String existCustCommPercent;
	
	@JsonProperty("NewCustCommPercent")
	private String newCustCommPercent;
	
	@JsonProperty("RenewCustCommPercent")
	private String renewCustCommPercent;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("Status")
	private String status;
	
}
