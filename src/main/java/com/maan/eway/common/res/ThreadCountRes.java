package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ThreadCountRes {

	@JsonProperty("Count")
	private Integer count ;
	
	@JsonProperty("GroupId")
	private Integer groupId ;
	
	@JsonProperty("GroupCount")
	private Integer groupCount ;
}
