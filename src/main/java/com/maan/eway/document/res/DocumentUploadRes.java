package com.maan.eway.document.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentUploadRes {

	@JsonProperty("Clientid")
	private String clientid;
}
