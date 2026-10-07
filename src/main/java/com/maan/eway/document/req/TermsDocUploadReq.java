package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsDocUploadReq {

	
	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("OriginalFileName")
	private String originalFileName;

	@JsonProperty("UploadedBy")
	private String uploadedBy;
	
	@JsonProperty("TermsAndCondtionId")
	private String termsAndCondtionId;
	
	@JsonProperty("TermsAndCondtionDesc")
	private String termsAndCondtionDesc;
	
	@JsonProperty("Type")
	private String type;
	
}
