package com.maan.eway.excelupload.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreviewRow {
	
	@JsonProperty("RowNumber")
	private int rowNumber;
	
	@JsonProperty("Values")
	private Map<String, Object> values;
	
	@JsonProperty("Valid")
	private boolean valid;
	
	@JsonProperty("ErrorMessage")
	private String errorMessage;
}
