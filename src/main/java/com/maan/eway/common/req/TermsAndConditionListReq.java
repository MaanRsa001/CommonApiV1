package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionListReq {

	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("SubId")
	private String subId;
	
	@JsonProperty("SubIdDesc")
	private String subIdDesc;
	
	@JsonProperty("DocRefNo")
	private String docRefNo;
	
	@JsonProperty("TypeId")
	private String typeId;
	
}
