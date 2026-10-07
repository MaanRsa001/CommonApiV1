package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteBusinessInterruptionRes {
	
	@JsonProperty("GrossProfitSi")
	private BigDecimal grossProfitSi;
	
	@JsonProperty("IndemnityPeriodSi")
	private BigDecimal indemnityPeriodSi;

}
