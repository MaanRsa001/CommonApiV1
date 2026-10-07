package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EserviceCustomerSearchVrtinReq {
	
	@JsonProperty("SearchValue")
	private String searchValue;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
}
