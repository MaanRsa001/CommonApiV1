package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentRes {
	
	@JsonProperty("DocumentType")
	private String documentType ;
	
	@JsonProperty("DocumentTypeDesc")
	private String documentTypeDesc;
	
	@JsonProperty("DocumentFile")
	private String orginalFileName ;
	
	@JsonProperty("DocumentId")
	private String documentId;
	
	@JsonProperty("DocumentDescription")
	private String documentDesc ;
	
	@JsonProperty("UniqueId")
	private Integer uniqueId;
	
	@JsonProperty("Originalpath")
	private String filePathOrginal;

	@JsonProperty("Compressed")
	private String filePathBackup;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("LocationId")
	private Integer locationId;
	
	@JsonProperty("LocationName")
	private String LocationName;
	
	@JsonProperty("SectionId")
	private Integer sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("DocApplicable")
	private String docApplicable ;
	
	@JsonProperty("DocApplicableId")
	private String docApplicableId ;
}
