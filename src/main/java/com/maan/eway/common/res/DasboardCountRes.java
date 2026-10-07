package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.builder.Builder;
import lombok.Data;

@Data
@Builder
public class DasboardCountRes {

	@JsonProperty("Position")
    private String     position     ;
	@JsonProperty("Status")
    private String     status     ;
	
	@JsonProperty("TotalCount")
    private String     count     ;
	
	@JsonProperty("Type")
    private String type;
	
	@JsonProperty("CurrencyCode")
    private String currencyCode;
	
	@JsonProperty("Premium")
    private BigDecimal premium;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
}
