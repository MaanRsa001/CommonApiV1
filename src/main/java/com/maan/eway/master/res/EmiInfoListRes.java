package com.maan.eway.master.res;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiInfoListRes implements Serializable {

    private static final long serialVersionUID = 1L;	

    
	@JsonProperty("PremiumWithTax")
	private String premiumWithTax;
	
	@JsonProperty("InstallmentPeriod")
	private String noOfMonth;
	
	@JsonProperty("InterestAmount")
	private String interestAmount;
	
	@JsonProperty("AdvanceAmount")
	private String advanceAmount;

	@JsonProperty("BalanceAmount")
	private String balanceAmount;

	@JsonProperty("InstallmentAmount")
	private String installment;

	@JsonProperty("TotalLoanAmount")
	private String totalLoanAmount;
	
	@JsonProperty("InstallmentTypeId")
	private String installmentTypeId;
	    
    @JsonProperty("InstallmentTypeDesc")
    private String installmentTypeDesc;

	

	
}
