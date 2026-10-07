package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveDepositeMasterRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	@JsonProperty("Status")
	private boolean status=false;
}
