package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TravelCoverageDetailsRes {

	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("CoverDesc")
	private String coverDesc;
	
	@JsonProperty("Coverage")
	private String coverage;
	
	
}
