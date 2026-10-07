package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Covers {
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("Premium")
	private String premium;

	@JsonProperty("IsSubCover")
	private String isSubCover;
}
