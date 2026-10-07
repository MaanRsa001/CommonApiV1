package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyCityMasterMultiInsertSaveReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("CityId")
	private String cityId;

	@JsonProperty("StateId")
	private String stateId;

	@JsonProperty("CountryId")
	private String countryId;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("InsuranceId")
	private String companyId;

}
