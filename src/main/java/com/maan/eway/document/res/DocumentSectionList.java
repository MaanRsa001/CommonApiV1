package com.maan.eway.document.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentSectionList {


	@JsonProperty("SectionId")
	private String sectionId;
	

	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("IdList")
	private List<DocumentDropdownRes> idList;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
}
