package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionDetailsReq {

	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("RiskId")
	private Integer vehicleId;
	
	@JsonProperty("Covers")
	private List<CoverIdReq2> coverIdList;
	
}
