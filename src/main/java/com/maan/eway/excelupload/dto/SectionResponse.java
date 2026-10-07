package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectionResponse {

	@JsonProperty("SectionId")
    private String sectionId;
	@JsonProperty("SectionName")
    private String sectionName;
	@JsonProperty("CoverList")
    private List<CoverResponse> covers;
}

