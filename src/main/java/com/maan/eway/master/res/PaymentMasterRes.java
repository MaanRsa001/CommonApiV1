package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class PaymentMasterRes {

	@JsonProperty("PaymentMasterId")
	private String paymentMasterId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("CashYn")
	private String cashYn;

	@JsonProperty("ChequeYn")
	private String chequeYn;
	
	@JsonProperty("CreditYn")
	private String creditYn;
	
	@JsonProperty("MobilePaymentYn")
	private String mobilePaymentYn;
	
	@JsonProperty("AmendId")
	private String amendId;
	
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("OnlineYn")
	private String onlineYn;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("OaCode")
	private String oaCode;
	
	
	@JsonProperty("RealpayYn")
	private String realpayYn;

	@JsonProperty("EftYn")
	private String eftYn;
	
	
}
