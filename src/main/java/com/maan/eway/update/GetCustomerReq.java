package com.maan.eway.update;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetCustomerReq {
	@JsonProperty("PolicyNo")
	private String policyNo;
}
