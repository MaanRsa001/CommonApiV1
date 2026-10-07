package com.maan.eway.master.res;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetAllNonSelectedBrokerProductMasterRes implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String companyId;

//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("EffectiveDateStart")
//	private Date effectiveDateStart;
	
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("EffectiveDateEnd")
//	private Date effectiveDateEnd;


	@JsonProperty("ProductName")
	private String productName;
	
//	@JsonProperty("OldProductName")
//	private String oldProductName;

//	@JsonProperty("PaymentYn")
//	private String paymentYn;
//
//	@JsonProperty("PaymentRedirUrl")
//	private String paymentRedirUrl;
//
//	@JsonProperty("AppLoginUrl")
//	private String appLoginUrl;

	@JsonProperty("Status")
	private String status;

//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("EntryDate")
//	private Date entryDate;

	@JsonProperty("ProductDesc")
	private String productDesc;
//
//	@JsonProperty("CoreAppCode")
//	private String coreAppCode;

//	@JsonProperty("AmendId")
//	private String amendId;
//
//	@JsonProperty("Remarks")
//	private String remarks;
	
//	@JsonProperty("ProductIconId")
//	private String productIconId;
//	
//	@JsonProperty("ProductIconName")
//	private String productIconName;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
//	@JsonProperty("CommissionVatYn")
//	private String commissionVatYn;
	
	@JsonProperty("CheckerYn")
	private String checkerYn;
	
//	@JsonProperty("MakerYn")
//	private String makerYn;
	
//	@JsonProperty("CustConfirmYn")
//	private String custConfirmYn;
	
//	@JsonProperty("CreatedBy")
//	private String createdBy;
	
//	@JsonProperty("RegulatoryCode")
//	private String regulatoryCode;
    
  
	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
	@JsonProperty("PolicyTypeDesc")
	private String policyTypeDesc;
	
//	@JsonProperty("CommissionPercent")
//	private String commissionPercent;
//	
//	@JsonProperty("BackDays")
//	private String backDays;
//
	@JsonProperty("CreditYn")
	private String creditYn;
	
	@JsonProperty("LoginId")
	private String loginId;

}
