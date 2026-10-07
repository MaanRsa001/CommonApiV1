package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OneTimeTableReq {

	@JsonProperty("InsuranceId")
	private String insuranceId;
}
