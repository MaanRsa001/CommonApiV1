package com.maan.eway.excelupload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TemplateDownloadRes {

	@JsonProperty("RequestReferenceNo")
    private String requestReferenceNo;
	@JsonProperty("Template")
    private String template;

}
