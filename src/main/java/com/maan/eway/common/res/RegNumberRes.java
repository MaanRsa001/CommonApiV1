package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegNumberRes {

	@JsonProperty("RegisterNumberQuotes")
	private List<GetRegNumberQuoteRes> registerNumberQuotes;
	
	@JsonProperty("TotalCount")
	private String totalCount ;
	
}
