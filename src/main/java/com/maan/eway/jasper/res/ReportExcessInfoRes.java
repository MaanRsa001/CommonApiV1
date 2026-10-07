package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportExcessInfoRes {

	@JsonProperty("ExcessAmount")
	private Double excessAmount;
	
	@JsonProperty("ExcessPercent")
	private Integer excessPercent;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("ExcessDescription")
	private String excessDescription;
	
}
