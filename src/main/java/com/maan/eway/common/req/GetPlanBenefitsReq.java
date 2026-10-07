package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetPlanBenefitsReq {
	@JsonProperty("InsuranceId")
	private String insuranceId;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("PlanTypeId")
	private String planTypeId; //indi/fami
	@JsonProperty("PolicyTypeId")
	private String policyTypeId; //section--europe asia
}
