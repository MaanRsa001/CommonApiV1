package com.maan.eway.claimintimation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ThirdPartyInfoRequest {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("DriverName")
	private String driverName;

	@JsonProperty("LicenseNo")
	private String licenseNo;

	@JsonProperty("Nationality")
	private String nationality;

	@JsonProperty("MobileNo")
	private Long mobileNo;

	@JsonProperty("DriverLiability")
	private String driverLiability;

	@JsonProperty("VehicleNumber")
	private String vehicleNumber;

	@JsonProperty("Make")
	private String make;

	@JsonProperty("Model")
	private String model;

	@JsonProperty("PlateNo")
	private Integer plateNo;

	@JsonProperty("ThirdPartyReference")
	private String thirdPartyReference;

	@JsonProperty("ThirdPartyType")
	private String thirdPartyType;
}
