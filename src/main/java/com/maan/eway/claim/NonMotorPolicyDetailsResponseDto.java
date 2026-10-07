package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class NonMotorPolicyDetailsResponseDto {

	@JsonProperty("PolicyInfo")
	private PolicyInfoDetailsDto policyInfo;

	@JsonProperty("NonMotorInfo")
	private NonMotorRes nonmotoInfo;

}
