package com.maan.eway.report.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportResponse {

	
	@JsonProperty("Response")
	private String response;

	@JsonProperty("ErrorMessage")
	private String errorMessage;
}
