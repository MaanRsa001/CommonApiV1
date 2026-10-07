package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallEndorsementRes {
	
	@JsonProperty("EndtTypeId")
	private String endtTypeId;

	@JsonProperty("Status")
	private String status;
}
