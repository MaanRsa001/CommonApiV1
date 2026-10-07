package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CityDropDownReq {

	@JsonProperty("CountryId")
	private String countryId;
	

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("RegionId")
	private String regionId;

	@JsonProperty("StateId")
	private String stateId;
}
