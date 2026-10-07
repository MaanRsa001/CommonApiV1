package com.maan.eway.coinsurance.res;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoInsuranceDetailsRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	@JsonProperty("sNo")
	private String sNo;

	@JsonProperty("companyId")
	private String companyId;

	@JsonProperty("productId")
	private String productId;

	@JsonProperty("productDesc")
	private String productDesc;

	@JsonProperty("insuranceCompanyId")
	private String insuranceCompanyId;

	@JsonProperty("insuranceCompanyDesc")
	private String insuranceCompanyDesc;

	@JsonProperty("sharePercentage")
	private String sharePercentage;

	@JsonProperty("coInsurerRole")
	private String coInsurerRole;

	@JsonProperty("policyStartDate")
	private String policyStartDate;

	@JsonProperty("policyEndDate")
	private String policyEndDate;

	@JsonProperty("sumInsuredLc")
	private String sumInsuredLc;

	@JsonProperty("sumInsuredFc")
	private String sumInsuredFc;

	@JsonProperty("premiumLc")
	private String premiumLc;

	@JsonProperty("premiumFc")
	private String premiumFc;

	@JsonProperty("taxAmountLc")
	private String taxAmountLc;

	@JsonProperty("taxAmountFc")
	private String taxAmountFc;

	@JsonProperty("isFinancialEndt")
	private String isFinancialEndt;

	@JsonProperty("endtStatus")
	private String endtStatus;

	@JsonProperty("endtDate")
	private String endtDate;

	@JsonProperty("endtBy")
	private String endtBy;

	@JsonProperty("endtCategDesc")
	private String endtCategDesc;

	@JsonProperty("endorsementRemarks")
	private String endorsementRemarks;

	@JsonProperty("endorsementEffDate")
	private String endorsementEffDate;

	@JsonProperty("endtPrevPolicyNo")
	private String endtPrevPolicyNo;

	@JsonProperty("endtPrevQuoteNo")
	private String endtPrevQuoteNo;

	@JsonProperty("endtCount")
	private String endtCount;

	@JsonProperty("isChargRefund")
	private String isChargRefund;

	@JsonProperty("endtTypeId")
	private String endtTypeId;

	@JsonProperty("endtTypeDesc")
	private String endtTypeDesc;

	@JsonProperty("endtPremiumLc")
	private String endtPremiumLc;

	@JsonProperty("endtPremiumTax")
	private String endtPremiumTax;

	@JsonProperty("endtCommission")
	private String endtCommission;

	@JsonProperty("entryDate")
	private String entryDate;

	@JsonProperty("status")
	private String status;

	@JsonProperty("EffectiveDateStart")
	private String effectiveDateStart;

	@JsonProperty("effectiveDateEnd")
	private String effectiveDateEnd;

}
