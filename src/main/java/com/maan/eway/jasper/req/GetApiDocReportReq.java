package com.maan.eway.jasper.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetApiDocReportReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("FileCode")
	private String fileCode;
	
	@JsonProperty("TravelYN")
	private String travelYN;
	
}
