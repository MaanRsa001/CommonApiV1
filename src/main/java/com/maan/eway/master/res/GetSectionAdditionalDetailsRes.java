package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetSectionAdditionalDetailsRes {

	
	@JsonProperty("SectionId")
	private Integer sectionId;
	
	@JsonProperty("SectionName")
	private String SectionName;
	
	@JsonProperty("AddDetailYn")
	private String addDetailYn; 	// AdditionalDetailYN

	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String Remarks;

	@JsonProperty("JsonPath")
	private String jsonPath; 	//file upload and save file path

	@JsonProperty("SaveUrl")
	private String saveUrl;

	@JsonProperty("GetUrl")
	private String getUrl;

	@JsonProperty("GetallUrl")
	private String getallUrl;
	
	@JsonProperty("FileName")
	private String fileName;
	
//	@JsonProperty("CreatedBy")
//	private String createdBy;
	
	@JsonFormat(pattern ="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern ="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
}
