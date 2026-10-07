package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteGoodsInTransitRes {
	
	@JsonProperty("TransportedBy")
	private String transportedBy; 

	@JsonProperty("ModeOfTransport")
	private String modeOfTransport; 

	@JsonProperty("GeographicalCoverage")
	private String geographicalCoverage; 

	@JsonProperty("SingleRoadSiFc")
	private Double singleRoadSiFc; 
	
	@JsonProperty("EstAnnualCarriesSiFc")
	private Double estAnnualCarriesSiFc;

}
