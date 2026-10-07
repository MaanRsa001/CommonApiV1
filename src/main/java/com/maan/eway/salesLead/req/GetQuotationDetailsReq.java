package com.maan.eway.salesLead.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetQuotationDetailsReq {

	@JsonProperty("EnquiryId")
	private String enquiryId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("SalesLoginId")
	private String salesLoginId;
	
}
