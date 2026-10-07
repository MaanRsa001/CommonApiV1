package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SaveDepositeMasterReq {
	
//	@JsonProperty("Payableyn")
//	private String payableyn;
	
	@JsonProperty("DepositAmount")
	private String depositAmount;
	
//	@JsonProperty("RefundAmount")
//	private String refundAmount;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("BrokerId")
	private String brokerId;
	
	@JsonProperty("Customerid")
	private String customerid;
	
//	@JsonProperty("PremiumAmount")
//	private String premiumAmount;
//	
//	@JsonProperty("BalanceAmount")
//	private String balanceAmount;
	
	@JsonProperty("Premium")
	private String premium;
	
//	@JsonProperty("PolicyInsuranceFee")
//	private String policyInsuranceFee;
//	
//	@JsonProperty("VatAmount")
//	private String vatAmount;
//	
//	@JsonProperty("ChargableType")
//	private String chargableType;
	
	@JsonProperty("DepositNo")
	private String depositNo;
	
	@JsonProperty("CbcNo")
	private String cbcNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	
}
