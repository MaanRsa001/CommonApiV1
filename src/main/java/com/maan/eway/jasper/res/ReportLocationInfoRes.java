package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportLocationInfoRes {

	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("LocationName")
	private String locationName;
	
	@JsonProperty("SectionDetails")
	private List<ReportSectionInfoRes> sectionDetails;
	
	
	
}
