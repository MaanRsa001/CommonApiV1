package com.maan.eway.document.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocGroupRes {
	
	@JsonProperty("DocApplicable")
	private String docApplicable;
	
	@JsonProperty("DocApplicableId")
	private String docApplicableId;
	
	
	@JsonProperty("DocTypes")
	private List<DocTypeRes2> docTypes ;

}
