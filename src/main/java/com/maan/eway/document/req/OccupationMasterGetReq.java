package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OccupationMasterGetReq {

	@JsonProperty("OccupationId")
	private String occupationId ;
	

	@JsonProperty("InsuranceId")
	private String insCompanyId ;
}
