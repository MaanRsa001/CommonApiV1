package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportCoverInfoRes {

	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("CoverName")
	private String covername;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("Annually")
	private String annually;
	
	@JsonProperty("Monthly")
	private String monthly;
	
}
