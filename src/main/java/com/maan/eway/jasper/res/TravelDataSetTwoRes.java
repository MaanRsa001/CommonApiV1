package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TravelDataSetTwoRes {

	@JsonProperty("BandDesc")
	private String bandDesc;
	
	@JsonProperty("CoverName")
	private String coverName;
	
	@JsonProperty("PlanTypeDesc")
	private String planTypeDesc;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("Rate")
	private String rate;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("TaxRate")
	private String taxRate;
	
	@JsonProperty("Premium")
	private String premium;
	
}
