package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetOptedSectionAdditionalInfoRes {

	@JsonProperty("SectionId")
	private Integer sectionId;

	@JsonProperty("SectionName")
	private String sectionName;

	@JsonProperty("AddDetailYn")
	private String addDetailYn;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String Remarks;

	@JsonProperty("JsonPath")
	private String jsonPath; //file path

	@JsonProperty("SaveUrl")
	private String saveUrl;

	@JsonProperty("GetUrl")
	private String getUrl;

	@JsonProperty("GetallUrl")
	private String getallUrl;
}
