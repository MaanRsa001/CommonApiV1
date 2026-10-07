package com.maan.eway.viewAll.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VehicleBasedInfo {
	
	@JsonProperty("VehicleIdDetails")
	private List<KeyAndValueDto> vehicledetails;

}
	