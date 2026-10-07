package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateVerifiedYnReq {

	
	@JsonProperty("UniqueId")
	private String uniqueId;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("DocumentId")
	private String documentId;
	
	@JsonProperty("VerifiedYN")
	private String verifiedYN;
}
