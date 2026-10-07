package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndtUpdatePremiumRes {

	@JsonProperty("EndtPremium")
	private BigDecimal endtPremium;
	
	@JsonProperty("EndtVatPremium")
	private BigDecimal endtVatPremium;
	
	@JsonProperty("ChargeOrRefund")
	private String chargeOrRefund;
	
	@JsonProperty("EndtVatPremium")
	private BigDecimal endtEarendPremium;
	
	@JsonProperty("EndtVatPremium")
	private BigDecimal endtEarendTax;
	
}
