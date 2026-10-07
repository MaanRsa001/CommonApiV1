package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetPaymentStatusReq {

	@JsonProperty("LoginId")
	private String LoginId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("Limit")
	private String limit;

	@JsonProperty("Offset")
	private String offset;


}
