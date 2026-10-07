package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetEmployeeCountRes {

	@JsonProperty("ActualCount")
	private Long actualCount;
	
	@JsonProperty("ExpectedCount")
	private Long expectedCount;
	
	@JsonProperty("UploadCount")
	private Long uploadCount;
	
}
