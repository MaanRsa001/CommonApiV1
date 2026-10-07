package com.maan.eway.document.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentTypeDetails {


	@JsonProperty("CommonDocuments")
	private CommonDoumentRes commonDocuments;
	
	@JsonProperty("InduvidualDocuments")
	private List<LocationWiseSections> induvidualDocuments;
}
