package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class AccessoriesRes {
	
	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	@JsonProperty("AccessoriesSumInusured")
	private Double accessoriesSumInsured;

}
