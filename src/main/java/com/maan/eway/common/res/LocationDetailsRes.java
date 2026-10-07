package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LocationDetailsRes {

	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("LocationName")
	private String locationName;
	
	@JsonProperty("SectionDetails")
	private List<SectionDetailsRes> sectionDetails;
}
