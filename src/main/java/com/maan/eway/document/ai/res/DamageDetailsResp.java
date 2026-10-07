package com.maan.eway.document.ai.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DamageDetailsResp {

	@JsonProperty("damagePart")
	private String damagePart;
	
	@JsonProperty("repairCostUSD")
	private String repairCostUSD;
	
	@JsonProperty("repairMaxCostINR")
	private String repairMaxCostINR;
	
	@JsonProperty("materialType")
	private String materialType;
	
	@JsonProperty("recommendation")
	private String recommendation;
	
	@JsonProperty("repairMinCostINR")
	private String repairMinCostINR;
	
	@JsonProperty("damagePercentage")
	private String damagePercentage;
	
	@JsonProperty("damageSide")
	private String damageSide;
}
