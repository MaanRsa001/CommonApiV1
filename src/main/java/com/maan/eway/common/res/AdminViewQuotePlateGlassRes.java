package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuotePlateGlassRes {
	
	@JsonProperty("PlateGlassSi")
    private BigDecimal plateGlassSi    ;
	
	@JsonProperty("PlateGlassType")
    private String plateGlassType;

}
