package com.maan.eway.salesLead.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetEnquiryDetailsReq {

	@JsonProperty("EnquiryId")
	private String enquiryId;
	
	@JsonProperty("LeadId")
	private String leadId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("UWCode")
	private String uwCode;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
}
