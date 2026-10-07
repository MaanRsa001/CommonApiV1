package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EserviceCustomerDetailsRes {
	
	   
	   @JsonProperty("ProductName")
	   private String     productName ;


    @JsonProperty("CustomerReferenceNo")
    private String   customerReferenceNo ;
    
    @JsonProperty("RequestReferenceNo")
    private String   requestReferenceNo ;
	
    @JsonProperty("ClientName")
	private String clientName;

    @JsonProperty("MiddleName")
    private String middleName;
    
    @JsonProperty("LastName")
    private String lastName;
    
    @JsonProperty("Title")
    private String titleDesc;
    
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
	
	@JsonProperty("SavedFrom")
	private String savedFrom;
	
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
	@JsonProperty("status")
	private String  status;
	@JsonProperty("Broker")
	private String Broker; 
	@JsonProperty("ProductId")
	private String ProductId;
	

		
}
