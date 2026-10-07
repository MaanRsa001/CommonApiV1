package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentStausRes {

	@JsonProperty("LoginId")
	private String LoginId;
	
//	@JsonProperty("BrokerName")
//	private String brokerName;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("UserType")
	private String userType;
	
//	@JsonProperty("SubUserType")
//	private String subUserType;

	@JsonProperty("ProductId")
	private String productId;
	
//	@JsonProperty("ProductName")
//	private String productName;

	@JsonProperty("InsuranceId")
	private String companyId;
	
//	@JsonProperty("CustomerReferenceNo")
//	private String customerReferenceNo;
//
//	@JsonProperty("RequestReferenceNo")
//	private String requestReferenceNo;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("PaymentId")
	private String paymentId;

	@JsonProperty("PaymentStatus")
	private String paymentStatus;
	
	@JsonProperty("PaymentTypedesc")
	private String paymentTypedesc;
	
	@JsonProperty("InceptionDate")
	private String   inceptionDate;
	@JsonProperty("EntryDate")
	private String entryDate ;
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	@JsonProperty("ApplicationId")
	private String   applicationId;
	@JsonProperty("LoginId")
	private String   loginId;
	
	
//	@JsonProperty("CustomerId")
//	private String customerId;
//
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("PolicyStartDate")
//	private Date policyStartDate;
//
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("PolicyEndDate")
//	private Date policyEndDate;
//
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("EntryDate")
//	private Date entryDate;

//	@JsonProperty("OverallPremiumLc")
//	private BigDecimal overallPremiumLc;
//
//	@JsonProperty("OverallPremiumFc")
//	private BigDecimal overallPremiumFc;
//
//	@JsonProperty("Currency")
//	private String currency;
//
//	@JsonProperty("EndorsementType")
//	private String endorsementType;
//	@JsonProperty("EndorsementTypeDesc")
//	private String endorsementTypeDesc;
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("EndorsementDate")
//	private Date endorsementDate;
//	@JsonProperty("EndorsementRemarks")
//	private String endorsementRemarks;
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("EndorsementEffdate")
//	private Date endorsementEffdate;
//	@JsonProperty("OriginalPolicyNo")
//	private String originalPolicyNo;
//	@JsonProperty("EndtPrevPolicyNo")
//	private String endtPrevPolicyNo;
//	@JsonProperty("EndtPrevQuoteNo")
//	private String endtPrevQuoteNo;
//	@JsonProperty("EndtCount")
//	private BigDecimal endtCount;
//	@JsonProperty("EndtStatus")
//	private String endtStatus;
//	@JsonProperty("EndtCategDesc")
//	private String endtCategDesc;
//
//	@JsonProperty("EndtPremium")
//	private Double endtPremium;
	
}
