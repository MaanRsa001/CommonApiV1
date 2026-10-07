package com.maan.eway.salesLead.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuoteInformationDTO {
	@JsonProperty("EnquiryId") 
    private String enquiryId;
	
	@JsonProperty("QuoteNo") 
    private String quoteNo;
	
	@JsonProperty("QuotationDescription") 
    private String quotationDescription;
	
	@JsonProperty("SumInsured") 
    private Double sumInsured;
	
	@JsonProperty("PremiumRate") 
    private Double premiumRate;
	
	@JsonProperty("PremiumAmount") 
    private Double premiumAmount;
	
	@JsonProperty("TechnicalDiscount") 
    private Double technicalDiscount;
	
	@JsonProperty("AdditionalDiscount") 
    private Double additionalDiscount;
	
	@JsonProperty("QuoteStatus") 
    private String quoteStatus;
	
	@JsonProperty("QuoteStatusDesc") 
    private String quoteStatusDesc;
	
	@JsonProperty("QuoteRemarks") 
    private String quoteRemarks;
	
	@JsonProperty("SalesRemarks") 
    private String salesRemarks;
	
	@JsonProperty("UWRemarks") 
    private String UWRemarks;
	
	@JsonProperty("LoginId") 
    private String loginId;
	
	@JsonProperty("LeadId") 
    private String leadId;
}
