package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyTypeSubCoverMasterGetRes {
	

	@JsonProperty("SubCoverId")
	private String subCoverId;
	
	@JsonProperty("SubCoverDesc")
	private String subCoverDesc;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("ExcessAmt")
	private String excessAmt;
	
	@JsonProperty("Status")
	private String status;

}
