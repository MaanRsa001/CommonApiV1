package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Cover {
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("Covers")
	private List<Covers> covers;
	

}
