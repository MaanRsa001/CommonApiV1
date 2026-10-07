package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalSearchReq {

	@JsonProperty("SearchType")
	private String searchType;
	
	@JsonProperty("InsuredMobile")
	private String insuredMobile;
	
	@JsonProperty("Platenumber")
	private String plateNumber;
}
