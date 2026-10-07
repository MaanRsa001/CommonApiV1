package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class GetOccupationsReq {
	
	@JsonProperty("SectionId")
	private String sectionId;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("QuoteNo")
	private String quoteNo;
}
