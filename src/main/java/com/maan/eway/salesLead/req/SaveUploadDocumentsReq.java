package com.maan.eway.salesLead.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SaveUploadDocumentsReq {

	@JsonProperty(value = "EnquiryId")
	private String enquiryId;
	
	@JsonProperty(value = "QuoteNo")
	private String quoteNo;
	
	@JsonProperty(value = "LoginId",required = true)
	private String loginId;
	
	@JsonProperty(value = "Status",required = true)
	private String status;
	
}
