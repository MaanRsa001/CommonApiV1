package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetRegNumberQuoteRes {

	   @JsonProperty("ProductName")
	   private String     productName ;


    @JsonProperty("CustomerReferenceNo")
    private String   customerReferenceNo ;
    
    @JsonProperty("RequestReferenceNo")
    private String   requestReferenceNo ;
	
    
    @JsonProperty("RegisterNumber")
    private String   registerNumber;
    
    @JsonProperty("ChassisNumber")
    private String   chassisNumber;
    
    
    @JsonProperty("ClientName")
	private String clientName;

	
	@JsonProperty("IdNumber")
	private String idNumber;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	  
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("RejectReason")
	private String rejectReason;
	
	@JsonProperty("AdminRemarks")
	private String adminRemarks;
	
	@JsonProperty("ReferalRemarks")
	private String referalRemarks;
	
	@JsonProperty("Count")
	private String   count ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("OverallPremiumLc")
	private BigDecimal overallPremiumLc;
	
	@JsonProperty("OverallPremiumFc")
	private BigDecimal overallPremiumFc;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("EndorsementType")
	private String endorsementType;
	@JsonProperty("EndorsementTypeDesc")
	private String endorsementTypeDesc;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EndorsementDate")
	private Date endorsementDate;
	@JsonProperty("EndorsementRemarks")
	private String endorsementRemarks;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EndorsementEffdate")
	private Date endorsementEffdate;
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	@JsonProperty("EndtPrevPolicyNo")
	private String endtPrevPolicyNo;
	@JsonProperty("EndtPrevQuoteNo")
	private String endtPrevQuoteNo;
	@JsonProperty("EndtCount")
	private BigDecimal endtCount;
	@JsonProperty("EndtStatus")
	private String endtStatus;
	@JsonProperty("EndtCategDesc")
	private String endtCategDesc;

	@JsonProperty("EndtPremium")
	private Double endtPremium;
	
}
