package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetSectionRes {

	@JsonProperty("OptedList")
	private List<DropdownResponse> optedSectionList;

	@JsonProperty("UnOptedList")
	private List<DropdownResponse> unOptedSectionList;

}
