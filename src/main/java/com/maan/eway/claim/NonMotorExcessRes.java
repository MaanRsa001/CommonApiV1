package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NonMotorExcessRes {
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("CoverName")
	private String coverName;
	
	@JsonProperty("ExcessId")
	private String excessId;
	
	@JsonProperty("ExcessDesc")
	private String excessDesc;
	
	@JsonProperty("ExcessCode")
	private String excessCode;
	
	@JsonProperty("ExcessAmount")
	private String excessAmount;
	
	@JsonProperty("ExcessPercent")
	private String excessPercent;
	


}
