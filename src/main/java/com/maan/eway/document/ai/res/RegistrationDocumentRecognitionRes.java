package com.maan.eway.document.ai.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegistrationDocumentRecognitionRes {

	@JsonProperty("RegistrationNumber")
	private String registrationNumber;
	
	@JsonProperty("ChassisNumber")
	private String chassisNumber;
	
	@JsonProperty("EngineNumber")
	private String engineNumber;
	
	@JsonProperty("Make")
	private String make;
	
	@JsonProperty("Model")
	private String model;
	
	@JsonProperty("VehicleColor")
	private String vehicleColor;
	
	@JsonProperty("BodyType")
	private String bodyType;
	
	@JsonProperty("EngineCapacity")
	private String engineCapacity;
	
	@JsonProperty("SeatingCapacity")
	private String seatingCapacity;
	
	@JsonProperty("ManufactureYear")
	private String manufactureYear;
	
	@JsonProperty("TareWeight")
	private String tareWeight;
	
	@JsonProperty("GrossWeight")
	private String grossWeight;
	
	@JsonProperty("MotorCategory")
	private String motorCategory;
	
	@JsonProperty("FuelType")
	private String fuelType;
	
	@JsonProperty("NumberofAxis")
	private String numberofAxis;
	
	@JsonProperty("VehicleStatus")
	private String vehicleStatus;
}
