package com.maan.eway.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TicketCreateReq {

	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("CustomerMail")
	private String customerMail;
	
	@JsonProperty("CustomerMobile")
	private String customerMobile;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
}
