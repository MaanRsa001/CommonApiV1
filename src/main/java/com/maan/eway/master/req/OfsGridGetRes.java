package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OfsGridGetRes {

	@JsonProperty("CoverageSubId")
	private String coverageSubId;
	
	@JsonProperty("CalcType")
	private String calcType;
	
	@JsonProperty("CalcTypeDesc")
	private String calcTypeDesc;
	
	@JsonProperty("BaseRate")
	private String baseRate;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("MinimumPremium")
	private String minimumPremium;
}
