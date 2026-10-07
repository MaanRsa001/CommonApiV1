package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverDocumentMasterSaveReq {

	@JsonProperty("DocumentId")
	private String documentId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CoverId")
	private String coverId;
		
	@JsonProperty("CreatedBy")
	private String createdBy;

}
