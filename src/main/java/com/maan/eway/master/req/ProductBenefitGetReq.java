package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductBenefitGetReq {

		@JsonProperty("InsuranceId")
		private String companyId;
		
		@JsonProperty("ProductId")
		private String productId;
	
		@JsonProperty("SectionId")
		private String sectionId;

		@JsonProperty("CoverId")
		private String coverId;
		
//		@JsonProperty("TypeId")
//		private String typeId;
		
		@JsonProperty("BenefitId")
		private String benefitId;
}
