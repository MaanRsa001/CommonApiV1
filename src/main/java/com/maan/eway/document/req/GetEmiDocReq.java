package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetEmiDocReq {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("EmiYn")
	private String emiYn;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;

	@JsonProperty("NoOfInstallment")
	private String noOfInstallment;
	
}
