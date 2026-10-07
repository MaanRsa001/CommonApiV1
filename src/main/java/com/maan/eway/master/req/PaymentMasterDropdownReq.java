package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentMasterDropdownReq {

	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	

	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
}
