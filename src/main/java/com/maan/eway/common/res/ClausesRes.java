package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClausesRes {

	@JsonProperty("Id")
	private String id;
		
	
	@JsonProperty("SubId")
	private String subId;

	@JsonProperty("SubIdDesc")
	private String subIdDesc;

@JsonProperty("DocRefNo")
private String docRefNo;

@JsonProperty("DocumentId")
private String documentId;

@JsonProperty("TypeId")
private String typeId;

@JsonProperty("SectionId")
private String sectionId;

@JsonProperty("CoverId")
private String coverId;

@JsonProperty("CoverDesc")
private String coverDesc;

}
