package com.maan.eway.excelupload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentItemResponse {
	
	@JsonProperty("contentId")
    private Long contentId;
	
	@JsonProperty("SectionId")
    private String sectionId;
	
	@JsonProperty("CoverId")
    private String coverId;
	
	@JsonProperty("Param1")
	private String param1;
	
	@JsonProperty("Value")
    private String value;
	
	@JsonProperty("Param3")
    private String param3;
	
	@JsonProperty("Param4")
    private String param4;
	
	@JsonProperty("Param5")
    private String param5;
	
	@JsonProperty("Param6")
    private String param6;
	
	@JsonProperty("Param7")
    private String param7;
	
	@JsonProperty("Param8")
    private String param8;
	
	@JsonProperty("Param9")
    private String param9;
	
	@JsonProperty("Param10")
    private String param10;
}
