package com.maan.eway.jasper.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PremiumReportReq {
	
	@JsonProperty("StartDate")
	private String startDate;
	
	@JsonProperty("EndDate")
	private String endDate;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("Code")
	private String code;
	
	@JsonProperty("UserType")
	private String userType;

	@JsonProperty("BusinessType")
	private String businessType;

	@JsonProperty("Limit")
	private String limit;
	
	@JsonProperty("Offset")
	private String offset;
	
	@JsonProperty("ExcelYn")
	private String excelYn;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	 @JsonProperty("PortFolioYn")
	 private String  portFolioYn;
}
