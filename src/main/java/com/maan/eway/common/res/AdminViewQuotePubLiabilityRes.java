package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuotePubLiabilityRes {
	
	@JsonProperty("LiabilitySi")
    private BigDecimal liabilitySi    ;
	
	@JsonProperty("ProductTurnoverSi")
    private BigDecimal productTurnoverSi    ;
	
//	@JsonProperty("AooSumInsured")
//    private BigDecimal aooSumInsured ;
	
//	@JsonProperty("AggSumInsured")
//    private BigDecimal aggSumInsured ;
	
	@JsonProperty("SumInsured")
	private BigDecimal sumInsured;

	@JsonProperty("Category")
    private String category;

}
