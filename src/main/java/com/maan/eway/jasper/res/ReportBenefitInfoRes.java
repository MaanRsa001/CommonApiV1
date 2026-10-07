package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportBenefitInfoRes {
	@JsonProperty("CoverLimit")
	private String coverLimit;
	
	@JsonProperty("CoverName")
	private String coverName;
}
