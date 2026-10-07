package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteBusinessRiskRes {
	
	@JsonProperty("AllriskSumInsured")
	private BigDecimal allriskSumInsured;

}
