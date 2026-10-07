package com.maan.eway.payment.process.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SavePaymentProcessReq {
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;

}
