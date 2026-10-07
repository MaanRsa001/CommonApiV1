package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductCurrDropDownReq {

	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("ProductId")
	private String productId;
}
