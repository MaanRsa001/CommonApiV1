package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StateDropDownReq {

	@JsonProperty("CountryId")
	private String countryId;
	

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("RegionCode")
	private String regionCode;


}
