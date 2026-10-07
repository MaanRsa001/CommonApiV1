package com.maan.eway.oman;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OmanDropDownRes {

	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("GovernorateId")
	private String governorateId;
	@JsonProperty("GovernorateDesc")
	private String governorateDesc;
	@JsonProperty("WilayatId")
	private String wilayatId;
	@JsonProperty("WilayatDesc")
	private String wilayatDesc;
	@JsonProperty("PostalCode")
	private String postalCode;
}
