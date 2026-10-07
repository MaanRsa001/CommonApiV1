package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetAllCustomerDetailsReq {

	@JsonProperty("InsuranceId")
	private String comapanyId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("Limit")
	private String limit;

	@JsonProperty("Offset")
	private String offset;

	@JsonProperty("CustomerType")
	private String customerType;
}
