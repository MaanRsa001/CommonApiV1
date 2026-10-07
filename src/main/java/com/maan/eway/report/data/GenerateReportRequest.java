package com.maan.eway.report.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GenerateReportRequest {

	@JsonProperty("polNo")
	private String polNo;
	
	@JsonProperty("polEndNo")
	private String polEndNo;
	
	@JsonProperty("reportType")
	private String reportType;

}
