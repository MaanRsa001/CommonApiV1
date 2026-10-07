package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CityDropdown {


	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	
	@JsonProperty("SubUrbDetails")
	List<SubUrbDropDown> subUrbDetails ;
	
	@JsonProperty("Status")
	private String status;
}
