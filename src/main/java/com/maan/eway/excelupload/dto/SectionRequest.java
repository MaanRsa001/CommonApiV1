package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionRequest {

	@JsonProperty("SectionId")
    private String sectionId;
	@JsonProperty("SectionName")
    private String sectionName;
	@JsonProperty("CoverList")
    private List<CoverRequest> coverList;
}
