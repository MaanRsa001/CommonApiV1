package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.res.PortfolioCustomerDetailsRes;

import lombok.Data;
@Data
public class GetallPortfolioActiveRes {
	
	@JsonProperty("Count")
	private Long   count ;

	@JsonProperty("PortfolioList")
    private List<PortfolioCustomerDetailsRes> custRes;
}
