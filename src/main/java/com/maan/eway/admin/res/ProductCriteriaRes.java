package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCriteriaRes {

	@JsonProperty("ProductId")
	private Integer productId ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("CompanyName")
	private String companyName ;
	
}
