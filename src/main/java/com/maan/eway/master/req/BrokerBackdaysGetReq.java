package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerBackdaysGetReq {

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;
}
