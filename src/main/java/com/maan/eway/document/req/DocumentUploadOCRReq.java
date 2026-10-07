package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentUploadOCRReq {
	
	@JsonProperty("FilePath")
	private String filePath;

	@JsonProperty("Value")
	private String value;
}
