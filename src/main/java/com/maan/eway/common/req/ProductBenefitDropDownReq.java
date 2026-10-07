package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductBenefitDropDownReq {

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("SubCoverId")
	private String subcoverid;
	
	
}
