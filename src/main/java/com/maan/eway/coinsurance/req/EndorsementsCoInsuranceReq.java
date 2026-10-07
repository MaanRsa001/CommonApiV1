package com.maan.eway.coinsurance.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementsCoInsuranceReq {
	
	 @JsonProperty("IsFinancialEndt")
	    private String isFinancialEndt;

	 @JsonProperty("EndtStatus")
	    private String endtStatus;

	 @JsonProperty("EndtDate")
	  private String endtDate;

	 @JsonProperty("EndtBy")
	  private String endtBy;

	 @JsonProperty("EndtCategDesc")	    
	 private String endtCategDesc;

	 @JsonProperty("EndorsementRemarks")	    
	 private String endorsementRemarks;

	 @JsonProperty("EndorsementEffDate")
	    private String endorsementEffDate;

	 @JsonProperty("EndtPrevPolicyNo")	    
	 private String endtPrevPolicyNo;

	 @JsonProperty("EndtPrevQuoteNo")	    
	 private String endtPrevQuoteNo;

	 @JsonProperty("EndtCount")
	    private String endtCount;

	 @JsonProperty("IsChargRefund")	    
	 private String isChargRefund;

	 @JsonProperty("EndtTypeId")	    
	 private String endtTypeId;

	 @JsonProperty("EndtTypeDesc")	    
	 private String endtTypeDesc;

	 @JsonProperty("EndtPremiumLc")	    
	 private String endtPremiumLc;

	 @JsonProperty("EndtPremiumTax")	    
	 private String endtPremiumTax;

	 @JsonProperty("EndtCommission")	    
	 private String endtCommission;

	 @JsonProperty("EntryDate")
	    private String entryDate;

	 @JsonProperty("EffectiveDateStart")
	    private String effectiveDateStart;

	 @JsonProperty("EffectiveDateEnd")
	    private String effectiveDateEnd;
}
