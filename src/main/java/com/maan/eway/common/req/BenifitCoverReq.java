package com.maan.eway.common.req;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BenifitCoverReq {

	@JsonProperty("LocationId")
	private Integer locationId;
	
	@JsonProperty("VehicleId")
	private Integer vehicleId;
	
	@JsonProperty("SectionId")
	private Integer sectionId;

	@JsonProperty("CoverId")
	private Integer coverId;

	@JsonProperty("UserOpt")
	private String userOpt;

	@JsonProperty("CoverageType")
	private String coverageType;
	
	@JsonProperty("CoverageLimit")
	private BigDecimal coverageLimit;

}
