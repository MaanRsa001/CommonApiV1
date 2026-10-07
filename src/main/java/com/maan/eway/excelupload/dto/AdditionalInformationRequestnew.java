package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdditionalInformationRequestnew {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	@JsonProperty("LocationList")
    private List<LocationRequest> locationList;
}
