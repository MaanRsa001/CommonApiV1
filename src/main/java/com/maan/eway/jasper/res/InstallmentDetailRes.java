package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InstallmentDetailRes {

	@JsonProperty("SrNo")
	private String srNo;
	
	@JsonProperty("DueDate")
	private String dueDate;
	
	@JsonProperty("Percentage")
	private String percentage;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("DueAmount")
	private String dueAmount;
	
	@JsonProperty("PaymentStatus")
	private String paymentStatus;
}
