package com.maan.eway.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerTrackDashboadRes {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("EntryDate")
	private String entryDate;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("CurrentStatus")
	private String currentStatus;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("SourceType")
	private String sourceType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("LoginId")
	private String loginId;
}
