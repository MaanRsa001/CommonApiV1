package com.maan.eway.yara.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YaraRiskDetails {

	@JsonProperty("DistributorId")
	private String distributorId;
	
	@JsonProperty("DistributorName")
	private String distributorName;
	
	@JsonProperty("YaraPackageYn")
	private String yaraPackageYn;
	
	@JsonProperty("Coverage")
	private String coverage;
	
	@JsonProperty("NoOfAcre")
	private String noOfAcre;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("PremiumAmount")
	private String premiumAmount;
	
	@JsonProperty("CoveragePercentage")
	private String coveragePercentage;
}
