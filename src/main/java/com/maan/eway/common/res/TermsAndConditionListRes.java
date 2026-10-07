package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionListRes {



	@JsonProperty("SubId")
	private String subId;
	
	@JsonProperty("SubIdDesc")
	private String subIdDesc;
		
	@JsonProperty("DocRefNo")
	private String docRefNo;

}
