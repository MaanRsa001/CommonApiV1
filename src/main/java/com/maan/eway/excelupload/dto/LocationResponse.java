package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationResponse {
	
	@JsonProperty("LocationId")
	private String locationId;
	@JsonProperty("SectionList")
	private List<SectionResponse> sectionList;

}
