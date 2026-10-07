package com.maan.eway.claimintimation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentInfoRequest {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("Document")
	private String document;

	@JsonProperty("Format")
	private String format;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("DocumentType")
	private String documentType;
}
