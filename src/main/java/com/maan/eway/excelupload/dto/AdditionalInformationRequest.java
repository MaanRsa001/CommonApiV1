package com.maan.eway.excelupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalInformationRequest {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
    
	@JsonProperty("ContentItems")
    private List<ContentItem> contentItems;
}
