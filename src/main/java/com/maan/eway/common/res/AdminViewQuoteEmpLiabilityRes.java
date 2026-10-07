package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteEmpLiabilityRes {
	
//	@JsonProperty("EmpLiabilitySi")
//	private BigDecimal empLiabilitySi;
	
	@JsonProperty("OccupationTypeDesc")
	private String occupationTypeDesc;
	
	@JsonProperty("totalNoOfEmployees")
	private Long TotalNoOfEmployees;
	
	@JsonProperty("SumInsured")
	private BigDecimal sumInsured;

}
