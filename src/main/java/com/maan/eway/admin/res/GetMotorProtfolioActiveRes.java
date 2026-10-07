package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetMotorProtfolioActiveRes {
	@JsonProperty("Count")
	private Long   count ;
	
	@JsonProperty("ProductName")
	private String   ProductName ;

	@JsonProperty("Portfolio")
    private List<PortfolioGridCriteriaRes> portfolioList;
}
