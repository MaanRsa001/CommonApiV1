package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TearmsAndCondition {

	@JsonProperty("AllConditions")
	private String allConditions;
	
}
