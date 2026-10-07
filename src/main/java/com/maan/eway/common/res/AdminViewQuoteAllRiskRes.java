package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteAllRiskRes {
	
	
	@JsonProperty("AllRiskSumInsured")
	private BigDecimal allRiskSumInsured;

}
