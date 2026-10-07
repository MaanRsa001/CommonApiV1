package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimCoverdetailsData {
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("ProductCode")
	private String productCode;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("SectionCode")
	private String sectionCode;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("CoverId")
	private String coverId;

	@JsonProperty("CoverCode")
	private String coverCode;
	
	@JsonProperty("CoverName")
	private String coverName;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("Suminsured")
	private String suminsured;
	
	@JsonProperty("ExcessAmount")
	private String excessAmount;
	
	@JsonProperty("ExcessPercent")
	private String excessPercent;
	
	@JsonProperty("CoverageLimit")
	private String coverageLimit;
	
	@JsonProperty("CoverageType")
	private String coverageType;
	
	@JsonProperty("EffectiveDate")
	private String effectiveDate;

}
