package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PremiumDetailsWithOuttax {
	
	@JsonProperty("CoverName")
	private String coverName; 
	
	@JsonProperty("CoverAgeLimite")
	private String coverAgeLimite;
	
	@JsonProperty("Rate")
	private String rate;
	
	@JsonProperty("PremiumIncludeTax")
	private String premiumIncludeTax;
	
	@JsonProperty("CoverAgeType")
	private String coverAgeType;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	


}
