package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LocationDetailsReq {

	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("SectionDetails")
	private List<SectionDetailsReq> sectiondetails;
	
}
