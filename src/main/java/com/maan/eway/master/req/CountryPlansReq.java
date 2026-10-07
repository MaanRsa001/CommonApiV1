package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CountryPlansReq {

	@JsonProperty("CountryId")
	private String countryId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("PlanType")

	private String planType;

}
