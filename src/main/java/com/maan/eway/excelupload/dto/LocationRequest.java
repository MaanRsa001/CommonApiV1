package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LocationRequest {

	@JsonProperty("LocationId")
    private String locationId;
	@JsonProperty("SectionList")
    private List<SectionRequest> sectionList;
}
