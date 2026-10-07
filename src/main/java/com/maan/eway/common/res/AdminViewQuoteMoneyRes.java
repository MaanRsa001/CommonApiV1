package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteMoneyRes {
	
	@JsonProperty("MoneyCollector")
	private BigDecimal moneyCollector;
	
	@JsonProperty("MoneyAnnualEstimate")
	private BigDecimal moneyAnnualEstimate;
	
	@JsonProperty("MoneyDirectorResidence")
	private BigDecimal moneyDirectorResidence;
	
	@JsonProperty("MoneySafeLimit")
	private BigDecimal MoneySafeLimit;
	
	@JsonProperty("MoneyMajorLoss")
	private BigDecimal moneyMajorLoss;
	
	@JsonProperty("MoneyOutofSafe")
	private BigDecimal MoneyOutofSafe;

}
