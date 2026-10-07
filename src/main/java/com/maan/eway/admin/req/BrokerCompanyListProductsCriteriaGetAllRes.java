package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCompanyListProductsCriteriaGetAllRes {

	
//	@JsonProperty("ProductId")
//	private String productId ;
//	
//	@JsonProperty("ProductName")
//	private String productName ;
//	
//	@JsonProperty("OldProductName")
//	private String oldProductName ;
//	
//	@JsonProperty("SumInsuredStart")
//	private String sumInsuredStart;
//	
//	@JsonProperty("SumInsuredEnd")
//	private String sumInsuredEnd;
//	
//
//	@JsonFormat(pattern="dd/MM/yyyy")
//	@JsonProperty("EffectiveDateStart")
//	private Date effectiveDateStart;
//	
//
//	@JsonFormat(pattern="dd/MM/yyyy")
//	@JsonProperty("EffectiveDateEnd")
//	private Date effectiveDateEnd;
//	
//
//	@JsonProperty("Status")
//	private String status;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("ProductDesc")
	private String productDesc;

	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
	@JsonProperty("PolicyTypeDesc")
	private String policyTypeDesc;
	
	@JsonProperty("CommissionPercent")
	private String commissionPercent;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("BackDays")
	private String backDays;

	@JsonProperty("CreditYn")
	private String creditYn;
	
	@JsonProperty("CheckerYn")
	private String checkerYn;
	
	@JsonProperty("MakerYn")
	private String makerYn;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	
}
