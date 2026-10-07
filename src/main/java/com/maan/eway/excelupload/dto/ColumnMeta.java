package com.maan.eway.excelupload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnMeta {

	@JsonProperty("HeaderName")
    private String headerName;
	@JsonProperty("DataType")
	private String dataType;
	 @JsonProperty("ApiKey")
	 private String apiKey; 
}

