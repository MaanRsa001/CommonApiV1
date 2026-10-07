package com.maan.eway.claimintimation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetIntimationReq {

	@JsonProperty("IntimationNo")
	private String intimationNo;
}
