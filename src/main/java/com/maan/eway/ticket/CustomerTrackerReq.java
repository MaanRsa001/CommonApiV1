package com.maan.eway.ticket;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerTrackerReq {

	@JsonProperty("Requestreferenceno")
	private String requestreferenceno ;
	
	@JsonProperty("Quoteno")
	private String quoteno ;
	
	@JsonProperty("CustomerName")
	private String customerName ;
	
	@JsonProperty("CompanyId")
	private String companyId ;
	
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("BranchName")
	private String branchName ;
	
	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	@JsonProperty("SectionId")
	private String sectionId ;
	
	@JsonProperty("SectionName")
	private String sectionName ;
	
	@JsonProperty("Email")
	private String email ;
	
	@JsonProperty("MobileNo")
	private String mobileNo ;
	
	@JsonProperty("CurrentStatus")
	private String currentStatus ;
	
	@JsonProperty("Remarks")
	private String remarks ;
	
	@JsonProperty("SourceType")
	private String sourceType ;
	
	@JsonProperty("SubUserType")
	private String subUserType ;
	
	@JsonProperty("LoginId")
	private String loginId ;
	
	
}
