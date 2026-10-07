package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccessoriesSumInsureDropDownRes {

	
	
//	@JsonProperty("Code")
//	private String code;
//	@JsonProperty("CodeDesc")
//	private String codeDesc;
//	@JsonProperty("AccessoriesSumInusured")
//	private String accessoriesSumInsured;
	
	
	@JsonProperty("VehicleAccessories")
	private List<AccessoriesRes> accessoriesRes;

	
	@JsonProperty("TotalAccessoriesSumInusured")
	private Double totalAccessoriesSumInsured;
}
