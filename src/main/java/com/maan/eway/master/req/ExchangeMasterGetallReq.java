package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeMasterGetallReq {

	
	@JsonProperty("CountryId")
	private String countryId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
}
