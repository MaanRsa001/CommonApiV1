package com.maan.eway.common.res;


import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortfolioByRegNoRes {

	@JsonProperty("AdminLoginId")
	private String adminLoginId;
	
	@JsonProperty("AdminRemarks")
	private String adminRemarks;
	
	@JsonProperty("ApplicationId")
	private String applicationId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;
	
	@JsonProperty("BrokerCode")
	private String brokerCode;
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("BrokerLoginId")
	private String loginId;
	
	@JsonProperty("CreditNo")
	private String creditNo;
	
	@JsonProperty("CurrencyCode")
	private String currency;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("DebitNo")
	private String debitNoteNo;
	
	@JsonProperty("EndorsementRemarks")
	private String endorsementRemarks;
	
	@JsonProperty("EndtSatus")
	private String endtStatus;
	
	@JsonProperty("ExchangeRate")
	private BigDecimal exchangeRate;
	
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	
	@JsonProperty("OverAllPremiumLc")
	private BigDecimal overallPremiumLc;
	
	@JsonProperty("OveralPremiumFc")
	private BigDecimal overallPremiumFc;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date expiryDate;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date quoteCreatedDate;
	
	@JsonProperty("ProductId")
	private Integer productId;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("ReferralRemarks")
	private String referalRemarks;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date entryDate;
	
	@JsonProperty("EndtStatusDesc")
	private String endStatusDesc;
	
	@JsonProperty("StatusDesc")
	private String statusDesc;
}
