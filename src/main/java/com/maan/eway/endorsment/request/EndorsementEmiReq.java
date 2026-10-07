package com.maan.eway.endorsment.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementEmiReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("ProductId")
	private String productId;

}
