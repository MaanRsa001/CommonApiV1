package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentMasterSaveReq {

	
	@JsonProperty("PaymentMasterId")
	private String paymentMasterId;
	
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
//	@JsonProperty("UserType")
//	private String userType;
//	
//	@JsonProperty("SubUserType")
//	private String subUserType;
	
	
	@JsonProperty("CashYn")
	private String cashYn;
	

	@JsonProperty("CreditYn")
	private String creditYn;
	
	
	@JsonProperty("ChequeYn")
	private String chequeYn;
	
	
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
	@JsonProperty("OnlineYn")
	private String onlineYn;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("OaCode")
	private String oaCode;
	
	@JsonProperty("MobilePaymentYn")
	private String mobilePaymentYn;
	
	@JsonProperty("RealpayYn")
	private String realpayYn;

	@JsonProperty("EftYn")
	private String eftYn;


	}
