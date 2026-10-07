package com.maan.eway.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetQuoteDetailsRes {
	
	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CreatedBy")
	private String loginId;
	
	@JsonProperty("CustomerId")
	private String customerId ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BrokerCode")
	private String brokerCode ;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("CountryCode")
	private String countryCode;
	
	@JsonProperty("Status")
	private String status ;
	
	@JsonProperty("PolicyNo")
	private String policyNo ;
	
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo ;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ExpiryDate")
	private Date expiryDate ;
	
	@JsonProperty("Currency")
	private String currency ;
	
	@JsonProperty("Remarks")
	private String  remarks ;
	
	@JsonProperty("Premium")
	private String premium ;
	
	@JsonProperty("ExcessSign")
	private String excessSign ; 
	
	@JsonProperty("ExcessPremium")
	private String excessPremium ;
	
	@JsonProperty("DiscountPremium")
	private String discountPremium ;
	
	@JsonProperty("PolicyFee")
	private String policyFee ;
	
	@JsonProperty("OtherFee")
	private String otherFee ; 
	
	@JsonProperty("VatPercent")
	private String vatPercent ;
	
	@JsonProperty("VatPremium")
	private String vatPremium ; 
	
	@JsonProperty("OverallPremium")
	private String overallPremium ; 
	
	@JsonProperty("CommissionPercentage")
	private String commissionPrecentage ; 
	
	@JsonProperty("VatCommission")
	private String vatCommission ;
	
	@JsonProperty("Commission")
	private String  commission ;
	
	@JsonProperty("CalcPremium")
	private String  calcPremium ;
	
	@JsonProperty("AdminRemarks")
	private String adminRemarks ;
	
    @JsonProperty("ReferralDescription")
    private String  referalDescription ;
    

	@JsonProperty("LapsedRemarks")
	private String lapsedRemarks;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("LapsedDate")
	private Date lapsedDate;
	
	@JsonProperty("ApplicationNo")
	private String applicationNo;
	
	@JsonProperty("RejectDesc")
	private String rejectDesc;
	
	@JsonProperty("DebitNoteNo")
	private String debitNoteNo;
	
	@JsonProperty("CreditNoteNo")
	private String creditNoteNo;
	
	@JsonProperty("ReceiptNo")
	private String receiptNo;
	
	@JsonProperty("CreditNo")
	private String creditNo;
	
	@JsonProperty("commision")
	private String commision;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	
	@JsonProperty("OrgStatus")
	private String orgStatus;
	
	@JsonProperty("OldReferenceNo")
	private String oldReferenceNo;
	
	@JsonProperty("ModeOfPayment")
	private String modeOfPayment;
	
	/*
	private String aaa_cardno;
	private String rsa_cardno;
	private String integstatus;
	private String integerrordesc;
	private String mobileno;
	*/
}
