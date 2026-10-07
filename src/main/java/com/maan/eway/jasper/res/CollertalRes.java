package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CollertalRes {

	@JsonProperty("BorrowerType")
	private String borrowerType;
	
	@JsonProperty("CollateralName")
	private String collateralName;
	
	@JsonProperty("FirstLossPayee")
	private String firstLossPayee;
}
