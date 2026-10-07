package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyProductMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;


	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("PaymentYn")
	private String paymentYn;

	@JsonProperty("PaymentRedirUrl")
	private String paymentRedirUrl;

	@JsonProperty("AppLoginUrl")
	private String appLoginUrl;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("ProductDesc")
	private String productDesc;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("ProductIconId")
	private String productIconId;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("CommissionVatYn")
	private String commissionVatYn;
	
	@JsonProperty("CheckerYn")
	private String checkerYn;
	
	@JsonProperty("MakerYn")
	private String makerYn;
	
	@JsonProperty("CustConfirmYn")
	private String custConfirmYn;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("CoInsurance_YN")
	private String coInsuranceYn;

	@JsonProperty("ClaimExperience_YN")
	private String claimExperienceYn;
	
}
