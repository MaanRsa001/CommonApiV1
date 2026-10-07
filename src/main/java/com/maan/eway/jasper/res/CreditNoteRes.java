package com.maan.eway.jasper.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreditNoteRes {
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("CreditNo")
	private String creditNo;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("Business")
	private String business;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("EndorsementNo")
	private String endorsementNo;
	
	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	
	@JsonProperty("EndtTypeDesc")
	private String endtTypeDesc;
	
	@JsonProperty("EndorsementRemarks")
	private String endorsementRemarks;
	
	@JsonProperty("InceptionDate")
	private String inceptionDate;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("ApprovedBy")
	private String approvedBy;
	
	@JsonProperty("OverAllPremiumFc")
	private String overAllPremiumFc;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CompanyLogo")
	private String companyLogo;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("VatRegNo")
	private String vatRegNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("SectionDescList")
	private List<CreditDataSetOne> sectionDescList;

	@JsonProperty("RiskCodeList")
	private List<CreditDataSetTwo> riskCodeList;
	
	@JsonProperty("PremiumDetails")
	private List<TaxInvoicePremiumDetails> premiumDetails;
	
	@JsonProperty("CompanyAddress")
	 private String companyAddress ;
	
	@JsonProperty("CompanyPhone")
	 private String companyPhone ;
	
	@JsonProperty("CompanyWebsite")
	 private String companyWebsite ;
	
	@JsonProperty("CompanyEmail")
	 private String companyEmail ;
	
	@JsonProperty("VrnNumber")
	 private String vrnNumber;
	
	@JsonProperty("CoreAppBrokerCode")
	 private String coreAppBrokerCode ;
	
	@JsonProperty("EffectiveDate")
	 private String effectiveDate ;
	
	@JsonProperty("UserName")
	 private String userName ;
	
	@JsonProperty("AmountInWords")
	private String amountInWords;
	
	@JsonProperty("Signature")
	private String signature;
	
	@JsonProperty("BdmName")
	 private String bdmName;
	
	@JsonProperty("AppBy")
	 private String appBy;
	
	@JsonProperty("CreatedBy")
	 private String createdBy;
	
}
