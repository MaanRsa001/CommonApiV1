package com.maan.eway.claim;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NonMotorCoverRes {
	
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
	
	@JsonProperty("CoverageLimit")
	private String coverageLimit;
	
	@JsonProperty("CoverageType")
	private String coverageType;
	
	@JsonProperty("EffectiveDate")
	private String effectiveDate;
	
	@JsonProperty("ExcessList")
	private List<NonMotorExcessRes> excessList;
	
	@JsonProperty("AdditionalInformation")
	private List<AdditionalInformationRes> additionalInformationList;

}
