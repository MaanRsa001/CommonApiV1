package com.maan.eway.report.data;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GenerateReportResponse {
	
	@JsonProperty("reportType")
	private String reportType;
	
	@JsonProperty("polNo")
	private String polNo;
	
	@JsonProperty("base64Files")
	private List<String> base64Files;
	
	@JsonProperty("status")
	private String status;

}
