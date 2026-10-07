package com.maan.eway.jasper.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportQuoteInfoRes {

	@JsonProperty("QuoteNo")
	private String   quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String   requestReferenceNo;
	
	@JsonProperty("CustomerId")
	private String   customerId;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("CompanyId")
	private String   companyId;
	
	@JsonProperty("BranchCode")
	private String   branchCode;
	
	@JsonProperty("ProductId")
	private String   productId;
	
	@JsonProperty("SectionId")
	private String   sectionId;
	
	@JsonProperty("LoginId")
	private String   loginId;
	
	@JsonProperty("AgencyCode")
	private String   agencyCode;
	
	@JsonProperty("BrokerCode")
	private String   brokerCode;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("ExpiryDate")
	private Date expiryDate;
	
	@JsonProperty("Status")
	private String status ;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("QuoteCreatedDate")
	private Date quoteCreatedDate ;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate ;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("InceptionDate")
	private Date   inceptionDate;
	
	@JsonProperty("Currency")
	private String   currency;
	
	@JsonProperty("Remarks")
	private String   remarks;
	
	@JsonProperty("AdminRemarks")
	private String   adminRemarks;
	
	@JsonProperty("ReferalRemarks")
	private String   referalRemarks;
	
	// No OF Vehicles
	@JsonProperty("NoOfVehicles")
	private String noOfVehicles ;
	
	@JsonProperty("PremiumFc")
	private String  premiumFc ;
	
	@JsonProperty("OverallPremiumFc")
	private String   overAllPremiumFc ;
	
	@JsonProperty("VatPremiumFc")
	private String   vatPremiumFc;
	
	@JsonProperty("VatPercent")
	private String  vatPercent;
	
	@JsonProperty("PremiumLc")
	private String   premiumLc ;
	
	@JsonProperty("OverallPremiumLc")
	private String   overAllPremiumLc ;
	
	@JsonProperty("VatPremiumLc")
	private String  vatPremiumLc ;
	
	@JsonProperty("EmiYn")
	private String     emiYn;
	
	@JsonProperty("InstallmentPeriod")
    private String     installmentPeriod ;
	
	@JsonProperty("InstallmentMonth")
    private String     installmentMonth;
	
	@JsonProperty("DueAmount")
    private String     dueAmount;

	@JsonProperty("ProductName")
    private String    productName ;
	
	@JsonProperty("CompanyName")
    private String    companyName ;
	
	@JsonProperty("BrokerBranchCode")
    private String     brokerBranchCode  ;

	@JsonProperty("AdminLoginId")
    private String     adminLoginId ;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SourceType")
	private String sourceType;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("policyNo")
	private String policyNo;


	@JsonProperty("CommissionPercentage")
	private String commissionPercentage;
	
	@JsonProperty("VatCommission")
	private String vatCommission;
	
	@JsonProperty("DebitNoteNo")
	private String     debitNoteNo ;
	
	@JsonProperty("CreditNo")
	private String     creditNo ;
	
	@JsonProperty("StickerNumber")
	private String stickerNumber;
	
	
}
