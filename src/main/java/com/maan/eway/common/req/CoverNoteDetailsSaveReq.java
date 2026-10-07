package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverNoteDetailsSaveReq {

	@JsonProperty("RequestId")
    private String     requestId    ;
	@JsonProperty("CompanyCode")
    private String     companyCode  ;
	@JsonProperty("SystemCode")
    private String     systemCode   ;
	@JsonProperty("CallBackUrl")
    private String     callBackUrl  ;
	@JsonProperty("InsuranceCompanyCode")
    private String     insuranceCompanyCode ;
	@JsonProperty("TranCompanyCode")
    private String     tranCompanyCode ;
	@JsonProperty("CoverNoteTypeId")
    private String    coverNoteTypeId ;
	@JsonProperty("CoverNoteNumber")
    private String     coverNoteNumber ;
	@JsonProperty("PrevConverNoteReferenceNumber")
    private String     prevConverNoteReferenceNumber ;
	@JsonProperty("SalePointCode")
    private String     salePointCode ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("CoverNoteStartDate")
    private Date       coverNoteStartDate ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("CoverNoteEndDate")
    private Date       coverNoteEndDate ;
	
	@JsonProperty("CoverNoteDesc")
    private String     coverNoteDesc ;
	@JsonProperty("OperativeClause")
    private String     operativeClause ;
	@JsonProperty("PaymentMode")
    private String    paymentMode  ;
	
	@JsonProperty("CurrenyCode")
    private String     currenyCode  ;
	@JsonProperty("ExchangeRate")
    private String     exchangeRate ;
	@JsonProperty("TotalPremiumExcludingTax")
    private String     totalPremiumExcludingTax ;
	@JsonProperty("TotalPremiumIncludingTax")
    private String     totalPremiumIncludingTax ;
	@JsonProperty("CommissionPaid")
    private String     commissionPaid ;
	@JsonProperty("CommissionRate")
    private String     commissionRate ;
	@JsonProperty("OfficerName")
    private String     officerName  ;
	@JsonProperty("OfficerTitle")
    private String     officerTitle ;
	@JsonProperty("ProductCode")
    private String     productCode  ;
	@JsonProperty("EndorsementType")
    private String    endorsementType ;
	@JsonProperty("EndorsementReason")
    private String     endorsementReason ;
	@JsonProperty("EndoresementPremiumEarned")
    private String     endoresementPremiumEarned ;
}
