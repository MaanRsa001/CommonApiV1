package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MotorPrivateDriverDetails {

	@JsonProperty("DriverId")
	private String driverId;
	
	@JsonProperty("DriverName")
	private String driverName;
	
	@JsonProperty("DriverTypeDesc")
	private String driverTypeDesc;

	@JsonProperty("DriverDOB")
	private String driverDOB;
	
	@JsonProperty("IDNumber")
	private String iDNumber;
	
	@JsonProperty("ChassisNumber")
	private String chassisNumber;
	
}
