package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuoteDetailsRes {

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
	
	@JsonProperty("AmendId")
    private String   amendId;
	
	@JsonProperty("LoginId")
	private String   loginId;
	
	@JsonProperty("ApplicationId")
	private String   applicationId;
	
	@JsonProperty("ApplicationNo")
	private String   applicationNo;
	
	@JsonProperty("AgencyCode")
	private String   agencyCode;
	
	@JsonProperty("AcExecutiveId")
	private String   acExecutiveId; 
	
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
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("LapsedDate")
	private Date lapsedDate ; 
	
	@JsonProperty("LapsedRemarks")
	private String lapsedRemakrs ;
	
	@JsonProperty("LapsedUpdatedBy")
	private String lapsedUpdatedBy ;
	
	@JsonProperty("Currency")
	private String   currency;
	
	@JsonProperty("Remarks")
	private String   remarks;
	
	@JsonProperty("AdminRemarks")
	private String   adminRemarks;
	
	@JsonProperty("ReferalRemarks")
	private String   referalRemarks;
	
	@JsonProperty("VehicleNo")
    private String   vehicleNo;
	
	@JsonProperty("ExchangeRate")
	private String   exchangeRate;
	
	@JsonProperty("LcExchangeRate")
	private String LcExchangeRate;
	
	
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
	@JsonProperty("FinalizeYn")
	private String  finalizeYn ;
	@JsonProperty("Tax1")
	private String  tax1 ;
	@JsonProperty("Tax2")
	private String  tax2;
	@JsonProperty("Tax3")
	private String  tax3;
	
	@JsonProperty("EmiYn")
	private String     emiYn;
	@JsonProperty("InstallmentPeriod")
    private String     installmentPeriod ;
	
	@JsonProperty("InstallmentMonth")
    private String     installmentMonth;
	
	@JsonProperty("DueAmount")
    private String     dueAmount;
	
	@JsonProperty("TinyUrl")
    private String     tinyUrl;
	
	@JsonProperty("ManualReferalYn")
    private String     manualReferalYn;
    
	@JsonProperty("SubUserType")
	private String subUserType;
	@JsonProperty("ProductName")
    private String    productName ;
	@JsonProperty("CompanyName")
    private String    companyName ;
	
	@JsonProperty("HavePromoCode")
    private String     havepromocode ;
	
	@JsonProperty("PromoCode")
    private String     promocode    ;
	
	@JsonProperty("BrokerBranchCode")
    private String     brokerBranchCode  ;

	@JsonProperty("AdminLoginId")
    private String     adminLoginId ;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("BdmCode")
	private String bdmCode;
	
	@JsonProperty("SourceType")
	private String sourceType;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("PrevPaymentType")
	private String prevPaymentType;
	
	@JsonProperty("PrevPaymentTypeDesc")
	private String prevPaymentTypeDesc;
	
	
	/*
	@JsonProperty("ExcessSign(null);
	@JsonProperty("ExcessPremium(null);
	@JsonProperty("DiscountPremium(null);
	@JsonProperty("PolicyFee(null);
	@JsonProperty("OtherFee(null);
	@JsonProperty("Commission(null);
	@JsonProperty("CommissionPercentage(null);
	@JsonProperty("VatCommission(nll);
	@JsonProperty("CalcPremium(null);
	@JsonProperty("AdminReferralStatus(null);
	@JsonProperty("AdminReferralStatus(null);
	@JsonProperty("ReferralDescription(null);
	@JsonProperty("ApprovedBy(null);
	@JsonProperty("ApprCanBy(null);"
 */
	@JsonProperty("EndtStatus")
	private String endtStatus;
	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	@JsonProperty("policyNo")
	private String policyNo;
	
	@JsonProperty("Endtcategdesc")
    private String     endtCategDesc ;
	@JsonProperty("Endorsementremarks")
    private String     endorsementRemarks ;
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("Endorsementeffdate")
    private Date       endorsementEffdate ;
	@JsonProperty("Endtprevpolicyno")
    private String     endtPrevPolicyNo ;
	@JsonProperty("Endtprevquoteno")
    private String     endtPrevQuoteNo ;
	@JsonProperty("Endtcount")
    private Integer    endtCount    ;
	@JsonProperty("EndtTypeDesc")
	private String endtTypeDesc;
	@JsonProperty("IsChargeOrRefund")
	private String isChargeOrRefund;

	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	
	@JsonProperty("EndtPremium")
	private BigDecimal endtPremium;
	
	@JsonProperty("EndtPremiumTax")
	private BigDecimal endtPremiumTax;
	
	@JsonProperty("TotalEndtPremium")
	private BigDecimal TotalEndtPremium;

	@JsonProperty("CommissionPercentage")
	private String commissionPercentage;
	
	@JsonProperty("VatCommission")
	private String vatCommission;
	
	//Payment Details
	@JsonProperty("MerchantReference")
    private String     merchantReference ;
	
	@JsonProperty("DebitNoteNo")
	private String     debitNoteNo ;
	
	@JsonProperty("CreditNo")
	private String     creditNo ;
	
	@JsonProperty("StickerNumber")
	private String stickerNumber;
	
	@JsonProperty("ValuationYN")
	private String valuationYN;
	
	@JsonProperty("CertificateNumber")
	private String certificateNumber;
	
}
