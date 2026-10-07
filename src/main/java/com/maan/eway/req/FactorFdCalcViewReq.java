package com.maan.eway.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorFdCalcViewReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	
	@JsonProperty("ProductId")
	private Integer productId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("SectionId")
	private Integer sectionId;
	
	@JsonProperty("VehicleId")
	private Integer vehicleId;
	
//	@JsonProperty("CoverId")
//	private String coverId;
}
