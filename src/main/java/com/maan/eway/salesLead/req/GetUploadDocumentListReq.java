package com.maan.eway.salesLead.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetUploadDocumentListReq {

	@JsonProperty("EnquiryId")
	private String enquiryId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Status")
	private String status;
	
}
