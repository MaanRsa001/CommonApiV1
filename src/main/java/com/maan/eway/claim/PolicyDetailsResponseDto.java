package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PolicyDetailsResponseDto {

	@JsonProperty("VehicleInfo")
	private MotorRes vehicleInfo;

	@JsonProperty("PolicyInfo")
	private PolicyInfoDetailsDto policyInfo;

}
