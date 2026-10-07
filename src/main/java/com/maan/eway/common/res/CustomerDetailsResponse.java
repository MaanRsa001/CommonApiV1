package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data

@AllArgsConstructor

@NoArgsConstructor

public class CustomerDetailsResponse {

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("CustomerName")
	private String customerName;

	@JsonProperty("PolicyNo")
	private String policyNo;

	@JsonProperty("EffectiveDate")
	private Date effectiveDate;

	@JsonProperty("ExpiryDate")
	private Date expiryDate;

	@JsonProperty("Currency")
	private String currency;

	@JsonProperty("PremiumLc")
	private BigDecimal premiumLc;

	@JsonProperty("SumInsured")
	private BigDecimal SumInsured;


}
 