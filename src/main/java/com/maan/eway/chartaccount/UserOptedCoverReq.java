package com.maan.eway.chartaccount;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserOptedCoverReq {
	
	@JsonProperty("VehicleId")
	private String vehicleId;
	
	@JsonProperty("CoverId")
	private String coverId;
	

}
