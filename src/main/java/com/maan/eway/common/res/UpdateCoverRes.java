package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class UpdateCoverRes {

	@JsonProperty("CoverList")
	private List<Cover> coverList ;
	
	@JsonProperty("Response")
	private String response ;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("SectionId")
	private String sectionId ;
	
}
