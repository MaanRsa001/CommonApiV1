package com.maan.eway.document.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentDetailRes {
	@JsonProperty("DocumentType")
    private String documentType;
	@JsonProperty("DocumentTypeDesc")
	private String documentTypeDesc;
	@JsonProperty("DocumentFile")
    private String documentFile;
	@JsonProperty("DocumentId")
    private String documentId;
	@JsonProperty("DocumentDescription")
    private String documentDescription;
	@JsonProperty("UniqueId")
    private Integer uniqueId;
	@JsonProperty("Originalpath")
    private String originalpath;
	@JsonProperty("Compressed")
    private String compressed;
	@JsonProperty("Id")
    private String id;
	@JsonProperty("IdType")
    private String idType;
	@JsonProperty("DocApplicable")
    private String docApplicable;
	@JsonProperty("DocApplicableId")
    private String docApplicableId;
	@JsonProperty("Base64Image")
    private String base64Image; 
}

