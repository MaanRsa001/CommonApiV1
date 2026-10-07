package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerSumInsuredRefReq {

	@JsonProperty("InsuranceId")
	private String insuranceId;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	@JsonProperty("SumInsured")
	private String sumInsured;
	@JsonProperty("LoginId")
	private String loginId;
}
