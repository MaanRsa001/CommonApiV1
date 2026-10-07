package com.maan.eway.excess.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExcessDetailsReq {

	@JsonProperty("ExcessId")
	private Integer excessId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("VehicleId")
	private String riskId;

	@JsonProperty("ExcessPercentage")
	private String excessPercentage;

	@JsonProperty("ExcessAmount")
	private String excessAmount;

	@JsonProperty("ExcessDescription")
	private String excessDescription;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("LocationId")
	private String locationId;
	
	

}
