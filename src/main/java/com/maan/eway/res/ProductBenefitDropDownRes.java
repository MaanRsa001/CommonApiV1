package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductBenefitDropDownRes {

	@JsonProperty("BenefitId")
	private String benefitId;
	
	@JsonProperty("BenefitDescription")
	private String benefitDescription;
	
	@JsonProperty("SectionDesc")
	private String sectionDesc;
	
	@JsonProperty("LongDesc")
	private String longDesc;
		
	@JsonProperty("CalcType")
	private String calcType;
	
	@JsonProperty("Value")
	private String value;
	
//	@JsonProperty("TypeId")
//	private String typeId;
//	
//	@JsonProperty("TypeDesc")
//	private String TypeDesc;
//	
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
	@JsonProperty("ProductBenefits")
	private List<ProductBenefits> productBenefits;
	
}
