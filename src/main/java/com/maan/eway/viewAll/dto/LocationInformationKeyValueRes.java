package com.maan.eway.viewAll.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationInformationKeyValueRes {
	
	@JsonProperty("LocationDetails")
	List<KeyAndValueDto> locationName;
	
	@JsonProperty("Sections")
	private List<SectionDetailsKeyValueDto> sectionName;
	
 

}
