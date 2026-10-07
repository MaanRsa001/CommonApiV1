package com.maan.eway.res.calc;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.res.ViewQuoteRes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitAndCredit {
	
	@JsonProperty("Policyno")
    private String     policyNo ;
	@JsonProperty("Quoteno")
    private String     quoteNo      ;
	@JsonProperty("Chgid")
    private BigDecimal chgId        ;
	@JsonProperty("Companyid")
    private String     companyId    ;
	@JsonProperty("Productid")
    private String     productId    ;
//	@JsonProperty("Sectionid")
//    private String     sectionId    ;
	@JsonProperty("Branchcode")
    private String     branchCode   ;
    
	@JsonProperty("Chargecode")
    private BigDecimal chargeCode   ;
	@JsonProperty("Docno")
    private String     docNo        ;
	@JsonProperty("Doctype")
    private String     docType      ;
	
//	@JsonProperty("RiskDesc")
//    private String     riskDesc ;
	
	
	@JsonProperty("Docid")
    private String     docId        ;
	@JsonProperty("AmountLC")
    private BigDecimal amountLc       ;
	@JsonProperty("AmountFC")
    private BigDecimal amountFc       ;
	
	@JsonProperty("Entrydate")
    private Date       entryDate    ;
	@JsonProperty("Drcrflag")
    private String     drcrFlag     ;
	
	@JsonProperty("Status")
    private String status;
	
//	@JsonProperty("RiskId")
//    private String riskId;
	
	@JsonProperty("ChargeAccountDesc")
    private String chargeAccountDesc;
	
	@JsonProperty("Narration")
    private String narration;
	
	@JsonProperty("DisplayOrder")
    private String displayOrder;
	
	/*@JsonProperty("TotalCommission")
    private BigDecimal totalCommission      ;*/
	
	@JsonProperty("ViewQuoteInfo")
	private ViewQuoteRes quoteInfo;
}
