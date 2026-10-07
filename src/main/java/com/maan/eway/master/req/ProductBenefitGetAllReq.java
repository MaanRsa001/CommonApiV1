package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductBenefitGetAllReq {

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
	
//	@JsonProperty("TypeId")
//	private String typeId;
	
}
