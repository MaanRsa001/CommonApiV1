package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentDetailsRes {
	
	@JsonProperty("IndividualDocumentRes")
	private List<DocumentRes> individualDocumentRes ;
	
	@JsonProperty("CommonDocumentRes")
	private List<DocumentRes> commonDocumentRes ;
}
