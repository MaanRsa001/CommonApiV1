package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegionDropDownReq {

	@JsonProperty("CountryId")
	private String countryId;
	

	@JsonProperty("InsuranceId")
	private String companyId;
}
