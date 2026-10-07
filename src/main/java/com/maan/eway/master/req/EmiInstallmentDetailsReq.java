package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiInstallmentDetailsReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("PremiumWithTax")
	private String premiumWithTax;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("PolicyType")
	private String policyType;
	
	@JsonProperty("InstallmentPeriod")
    private String installmentPeriod ;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("UserOption")
	private String userOption;
	
	@JsonProperty("UserCustomizedEmiReq")
	private UserCustomizedEmiReq userCustomizedEmiReq;
}
