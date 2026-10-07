package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SavedepositDetailReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CbcNo")
	private String cbcNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("PremiumAmount")
	private String premiumAmount;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("DepositNo")
	private String depositNo;
	
	@JsonProperty("BalanceAmount")
	private String balanceAmount;
	
	@JsonProperty("ReceiptNo")
	private String receiptNo;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("PolicyInsuranceFee")
	private String policyInsuranceFee;
	
	@JsonProperty("VatAmount")
	private String vatAmount;
	
	@JsonProperty("ChargableType")
	private String ChargableType;
	
}
