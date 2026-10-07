package com.maan.eway.document.ai.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetVehicleDamageRes {

	@JsonProperty("vehicleNumber")
	private String vehicleRegNo;
	
	@JsonProperty("vehicleColor")
	private String vehicleColor;
	
	@JsonProperty("vehicleModel")
	private String vehicleModel;
	
	@JsonProperty("vehicleType")
	private String vehicleType;
	
	@JsonProperty("vehicleMake")
	private String vehicleMake;
	
	@JsonProperty("damagedParts")
	private List<DamageDetailsResp> damageDetails;
	

	
	
	
}
