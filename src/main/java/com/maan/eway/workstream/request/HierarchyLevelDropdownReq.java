package com.maan.eway.workstream.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HierarchyLevelDropdownReq {

	@JsonProperty("InsuredId")
	private String companyId;
	
	@JsonProperty("ItemType")
	private String itemType;
}
