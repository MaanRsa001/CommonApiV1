package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PdfResult {

	@JsonProperty("Base64")
    private String base64;
	
	@JsonProperty("FileName")
    private String fileName;
}
