package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyRegionMultiInsertSaveReq {

	@JsonProperty("RegionCode")
	private String regionCode;

	@JsonProperty("CreatedBy")
	private String createdBy;
		
	@JsonProperty("CountryId")
	private String countryId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
}
