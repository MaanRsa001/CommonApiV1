package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveSaveReq {

	@JsonProperty("AcExecutiveId")
	private String acExecutiveId;
	
	@JsonProperty("AcExecutiveName")
	private String acExecutiveName;
	
	@JsonProperty("ProductId")
	private List<String> productId;
	
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
	
	@JsonProperty("BankName")
	private String bankName;
	
	@JsonProperty("OaCode")
	private String oaCode;
	
}
