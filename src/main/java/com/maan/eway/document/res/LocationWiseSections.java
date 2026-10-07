package com.maan.eway.document.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LocationWiseSections {

	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("LocationName")
	private String locationName;

	
	@JsonProperty("SectionList")
	private List<DocumentSectionList> sectionList;
}
