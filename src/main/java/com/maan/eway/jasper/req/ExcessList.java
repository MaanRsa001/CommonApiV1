package com.maan.eway.jasper.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExcessList {

	@JsonProperty("Excessdesc")
	private String excessdesc;
	
	@JsonProperty("Excessamount")
	private String excessamount;
	
	@JsonProperty("Excessper")
	private String excessper;
}
