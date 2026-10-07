package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveUpdateReq {

	@JsonProperty("AcExecutiveId")
	private String acExecutiveId;
	
	@JsonProperty("AcExecutiveName")
	private String acExecutiveName;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	

	@JsonProperty("BankCode")
	private String bankCode;
	

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
	
}
