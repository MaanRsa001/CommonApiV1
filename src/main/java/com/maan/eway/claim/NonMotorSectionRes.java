package com.maan.eway.claim;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class NonMotorSectionRes {

	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionCode")
	private String sectionCode;
	
	@JsonProperty("SectionName")
	private String sectionDesc;
	
	@JsonProperty("SectionType")
	private String sectionType;
	
	@JsonProperty("CoverList")
	private List<NonMotorCoverRes> coverList;


}
