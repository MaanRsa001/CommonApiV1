package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchROPVehicleDetailsRes {
	

	@JsonProperty("VehicleDetails")
	private List<SearchROPVehicleRes> vehDetails;

}

