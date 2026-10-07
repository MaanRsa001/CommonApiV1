package com.maan.eway.finanaceIntegration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FinanceIntegerationBean {
	@JsonProperty("AccountType")
	private String accountType;
	@JsonProperty("LoginUserId")
	private String loginUserId;
	@JsonProperty("AcntType")
	private String acntType;
	@JsonProperty("PolicyNo")
	private String policyNo;
	@JsonProperty("QuoteNo")
	private String quoteNo;
	@JsonProperty("AdAhSysId")
	private String ad_ah_sysId;
	@JsonProperty("InsuranceId")
	private String companyId;
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
}
