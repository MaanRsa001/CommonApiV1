package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaxDataSetOneRes {
	
	@JsonProperty("RegistrationNumber")
	private String registrationNumber;
	
	@JsonProperty("MotorCategoryDesc")
	private String motorCategoryDesc;

}
