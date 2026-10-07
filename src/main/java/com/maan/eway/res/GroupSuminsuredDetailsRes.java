package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GroupSuminsuredDetailsRes {

	
	@JsonProperty("GroupId")
	private String groupId;
	
	@JsonProperty("GroupDesc")
	private String groupDesc;
	
	@JsonProperty("GroupCount")
	private String groupCount;
	
	@JsonProperty("GroupSuminsured")
	private String groupSuminsured;
}
