package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductBenefits {


	@JsonProperty("Code")
	private String code;
	
	@JsonProperty("CodeDesc")
	private String codeDesc;
	
	@JsonProperty("Image")
	private String image;
}
