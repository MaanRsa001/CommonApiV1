package com.maan.eway.reinsurance.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReInsuranceUnfreezeRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("Polidx")
	private String polidx;

	@JsonProperty("UwsysId")
	private String uwsysId;

	@JsonProperty("FreezeYN")
	private String freezeYN;

}
