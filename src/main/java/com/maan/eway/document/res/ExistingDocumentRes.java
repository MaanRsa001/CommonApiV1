package com.maan.eway.document.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExistingDocumentRes {
	@JsonProperty("IndividualDocumentRes")
    private List<DocumentDetailRes> individualDocumentRes;
	@JsonProperty("CommonDocumentRes")
    private List<DocumentDetailRes> commonDocumentRes;
}
