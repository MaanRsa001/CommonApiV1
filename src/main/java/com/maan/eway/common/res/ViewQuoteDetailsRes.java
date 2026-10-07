package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ViewQuoteDetailsRes {

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("InceptionDate")
	private Date inceptionDate;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ExpiryDate")
	private Date expiryDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("QuoteCreatedDate")
	private Date quoteCreatedDate;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyConvertedDate")
	private Date policyCovertedDate;

	@JsonProperty("PolicyPeriod")
	private String policyPeriod;

	@JsonProperty("Currency")
	private String currency;

	@JsonProperty("ExchangeRate")
	private String exchangeRate;

	@JsonProperty("CommissionPercentage")
    private String commissionPercentage;	

	@JsonProperty("Commission")
	private String commission;
	
	@JsonProperty("VatCommission")
    private String vatCommission;

	@JsonProperty("OverallPremiumFc")
	private String overallPremiumFc;

	@JsonProperty("OverallPremiumLc")
	private String overallPremiumLc;
	
	@JsonProperty("CreditNo")
	private String creditNo;
	
	@JsonProperty("DebitNoteNo")
	private String debitNoteNo;


	@JsonProperty("Loginid")
	private String loginid;
	@JsonProperty("Applicationid")
	private String applicationid;
	@JsonProperty("Sourcetype")
	private String sourcetype;
	@JsonProperty("CustomerCode")
	private String customerCode;
	@JsonProperty("CustomerCodeName")
	private String customerName;
	@JsonProperty("Division")
	private String branchCode;

	@JsonProperty("ReferralStatus")
	private String adminReferralStatus;
	@JsonProperty("ReferralRemarks")
	private String referralDescription;  //not in hpm
	@JsonProperty("AdminRemarks")
	private String adminRemarks;
	@JsonProperty("PremiaIntegrationStatus")
	private String premiaIntegrationStatus;
	@JsonProperty("TirraIntegrationStatus")
	private String tirraIntegrationStatus;
	
	// if endt presents
	@JsonProperty("EndtType")
	private String endtTypeId;
	@JsonProperty("EndtTypeDesc")
	private String endtTypeDesc;
	@JsonProperty("EndtPremium")
	private String endtPremium;
	@JsonProperty("EndtCount")
	private String endtCount;
	
	@JsonProperty("EndorsementYn")
	private String endorsementYn;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	@JsonProperty("StrickerNo")
	private String strickerNo;
	@JsonProperty("CoverNoteNo")
	private String covernoteNo;
	@JsonProperty("PaymentStatus")
	private String paymentStatus;
	@JsonProperty("PaymentMode")
	private String paymentMode;




}
