package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchReq {
	
	@JsonProperty("SearchKey")
	private String searchKey;

	@JsonProperty("SearchValue")
	private String searchValue;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("ApplicationId")
	private String applicationId;

	@JsonProperty("UserType")
	private String userType;

	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("MotorCategory")	
	private String motorCategory;
	
	@JsonProperty("VehicleMake")
	private String vehicleMake;
	
	@JsonProperty("VehicleModel")
	private String vehcileModel;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("VehicleType")
	private String vehicleType;
	
	@JsonProperty("SectionId")
	private String sectionId;

	
//	@JsonProperty("PlateNumber")
//	private String plateNo;
	
}
