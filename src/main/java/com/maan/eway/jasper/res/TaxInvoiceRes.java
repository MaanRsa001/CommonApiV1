package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TaxInvoiceRes {
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("ApprovedBy")
	private String approvedBy;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("VrTinNo")
	private String vrTinNo;
	
	@JsonProperty("IdentificationNo")
	private String identificationNo;
	
	@JsonProperty("IdentificationName")
	private String identificationName;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("InceptionDate")
	private String inceptionDate;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("DebitNoteNo")
	private String debitNoteNo;
	
	@JsonProperty("VrnNumber")
	private String vrnNumber;
	
	@JsonProperty("TinNumber")
	private String tinNumber;
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("OverAllPremium")
	private String overAllPremium;
	
	@JsonProperty("TotSumInsured")
	private String totSumInsured;
	
	@JsonProperty("IntermediaryRefNo")
	private String intermediaryRefNo;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("BankAccountName")
	private String bankaccountName;
	
	@JsonProperty("BankAccountNumber")
	private String bankaccountNumber;
	
	@JsonProperty("BankAddress")
	private String bankaddress;
	
	@JsonProperty("BankSwiftCode")
	private String bankswiftCode;
	
	@JsonProperty("CompanyLogo")
	private String companyLogo;
	
	@JsonProperty("CompanyWebsite")
	private String companyWebsite;
	
	@JsonProperty("CompanyMail")
	private String companyMail;
	
	@JsonProperty("CompanyPhone")
	private String companyPhone;
	
	@JsonProperty("CompanyAddress")
	private String companyAddress;
	
	@JsonProperty("CompanyPoBox")
	private String companyPoBox;
	
	@JsonProperty("AmountInWords")
	private String amountInWords;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BankaccountUSD")
	private String bankaccountUSD;
	
	@JsonProperty("BankName")
	private String bankName;
	
	@JsonProperty("BankBranchName")
	private String bankBranchName;
	
	@JsonProperty("BankBranchCode")
	private String bankBranchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("PolicyType")
	private String policyType;
	
	@JsonProperty("InstallmentDetail")
	private List<InstallmentDetailRes> installmentDetail;
	
	@JsonProperty("PremiumDetails")
	private List<TaxInvoicePremiumDetails> premiumDetails;
	
	@JsonProperty("Dataset1List")
	private List<TaxDataSetOneRes> dataset1List;

	@JsonProperty("VatPercent")
	private String vatPercent;
	
	@JsonProperty("VatAmount")
	private String vatAmount;
	
	@JsonProperty("CoreAppBrokerCode")
	private String coreAppBrokerCode ;
	
	@JsonProperty("EffectiveDate")
	private String effectiveDate ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	@JsonProperty("EmiYn")
	private String emiYn;
	
//	@JsonProperty("AttachMents")
//	private List<AttachMentRes> attachMents;
	
	@JsonProperty("Signature")
	 private String signature;
	
	@JsonProperty("BdmName")
	 private String bdmName;
	
	@JsonProperty("OrangeCardurl")
	private String orangeCardurl;
	
	@JsonProperty("AppBy")
	 private String appBy;
	
	@JsonProperty("CreatedBy")
	 private String createdBy;
	
}