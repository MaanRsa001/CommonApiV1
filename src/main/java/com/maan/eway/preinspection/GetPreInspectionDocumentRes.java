package com.maan.eway.preinspection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetPreInspectionDocumentRes {
	
	@JsonProperty("DocumentName")
	private String documentName;
	
	@JsonProperty("DocumentId")
	private String documentId;

}
