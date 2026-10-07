package com.maan.eway.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TaxListRes {

	@JsonProperty("CalcType")
	private String calcType;
	
	@JsonProperty("TaxCode")
	private String taxCode;
	
	@JsonProperty("TaxRate")
	private BigDecimal taxRate;
	
	@JsonProperty("TaxId")
	private Integer taxId;
	
	@JsonProperty("TaxName")
	private String taxName;
	
	@JsonProperty("TaxAmount")
	private BigDecimal taxAmount;
	
}
