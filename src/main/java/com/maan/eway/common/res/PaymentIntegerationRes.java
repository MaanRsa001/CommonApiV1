package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentIntegerationRes {
	
	@JsonProperty("QuoteNo")
	private String   quoteNo;
	
//	@JsonProperty("PolicyNo")
//	private String  policyNo;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("CompanyId")
	private String   companyId;
	
	@JsonProperty("CompanyName")
	private String   companyName;
	
	@JsonProperty("BranchCode")
	private String   branchCode;
	
	@JsonProperty("BranchName")
	private String   branchName;
	
	@JsonProperty("ProductId")
	private String   productId;
	
	@JsonProperty("ProductName")
	private String   productName;
	
	@JsonProperty("LoginId")
	private String   loginId;
	
	@JsonProperty("PaymentTypeId")
	private String   paymentTypeId;
	
	@JsonProperty("PaymentTypeName")
	private String   paymentTypeName;
	
	@JsonProperty("MerchantReference")
	private String merchantReference;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
	@JsonProperty("CurrencyId")
	private String currencyId;
	
	@JsonProperty("PaymentStatus")
	private String paymentStatus;
	
	@JsonProperty("EmiYn")
	private String emiYn;
	
	@JsonProperty("PolicyStartDate")
	private String policyStartDate;
	
	@JsonProperty("PolicyEndDate")
	private String policyEndDate;
	
	

}
