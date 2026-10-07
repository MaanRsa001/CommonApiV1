package com.maan.eway.upr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UprReq {
	
	@JsonProperty("PolicyNo")
	private String policyNo;
}
