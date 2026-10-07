package com.maan.eway.excelupload.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverRequest {
	
	
	@JsonProperty("CoverId")
    private String coverId;
	@JsonProperty("AdditionalInfoList")
    private List<Map<String, Object>> additionaInfo;
}
