package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxInvoicePremiumDetails {

	@JsonProperty("Amount")
	private String amount;
	
	@JsonProperty("Narration")
	private String narration;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("Rate")
	private String rate;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Currency")
	private String currency;
	
}
