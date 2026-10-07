package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PullrenewalReq {
	
	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("days")
	private Integer days;
}
