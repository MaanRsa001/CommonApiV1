package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EserviceBuildingSectionRes {

	@JsonProperty("MSRefNo")
	private String msrefno;
	
	 @JsonProperty("CdRefNo")
	 private String cdRefNo;

	@JsonProperty("VdRefNo")
	private String asRefNo;

	@JsonProperty("SectionId")
	private String sectionId;
}
