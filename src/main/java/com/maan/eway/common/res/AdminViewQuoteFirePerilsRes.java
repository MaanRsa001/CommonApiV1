package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteFirePerilsRes {
	
	@JsonProperty("BuildingSumInsured")
	private BigDecimal buildingSumInsured;
	
	@JsonProperty("IndemityPeriod")
	private String indemityPeriod;

	@JsonProperty("MakutiYn")
	private String makutiYn;
	
}
