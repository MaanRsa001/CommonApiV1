package com.maan.eway.yara.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YaraDocumentReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
}
