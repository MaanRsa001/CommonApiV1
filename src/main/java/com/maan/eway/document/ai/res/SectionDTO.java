package com.maan.eway.document.ai.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class SectionDTO {
	@JsonProperty("SectionId")
    private Integer sectionId;
	@JsonProperty("DocumentId")
    private Integer documentId;
	@JsonProperty("DocumentIdName")
    private String documentIdName;
	@JsonProperty("DocumentType")
    private String documentType;
	@JsonProperty("DocumentTypeName")
    private String documentTypeName;
	@JsonProperty("DocumentName")
    private String documentName;
	@JsonProperty("DamagePartsDetails")
    private List<DamagePartsDetailsDTO> damagePartsDetails;
}
