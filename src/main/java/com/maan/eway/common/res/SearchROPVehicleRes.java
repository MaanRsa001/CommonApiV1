package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchROPVehicleRes {

	@JsonProperty("RiskId")
	private Integer riskId;


	@JsonProperty("Make")
	private String resMake;

	@JsonProperty("Model")
	private String resModel;

	@JsonProperty("VehicleType")
	private String resBodyType;
	
	@JsonProperty("RegistrationNo")
	private String resRegNumber;

	@JsonProperty("ChassisNumber")
	private String resChassisNumber;
	
	@JsonProperty("EngineNumber")
	private String resEngineNumber;
	
	

	@JsonProperty("Color")
	private String resColor;

	@JsonProperty("YearOfManufacture")
	private Integer resYearOfManufacture;
	
	@JsonProperty("MotorDesc")
	private String motorDesc; //commercial

	@JsonProperty("MotorCategory")
	private String motorCategory; //Motor Cycle
	
	@JsonProperty("FuelType")
	private String fuelType;
	
	@JsonProperty("VehicleUsage")
	private String vehicleUsage;
	
	@JsonProperty("EngineCapacity")
	private String engineCapacity;
	
	@JsonProperty("SeatingCapacity")
	private Integer seatingCapacity;
	
	@JsonProperty("GrossWeight")
	private Double grossWeight;
	
	@JsonProperty("TareWeight")
	private Double tareWeight;
	
	@JsonProperty("PolicyType")
	private String policyType;
	
	@JsonProperty("SumInsured")
	private BigDecimal sumInsured;
	
	@JsonProperty("StickerNo")
	private String stickerNo;
	
	@JsonProperty("CoverNoteRefNo")
	private String covernoterefno;
	
	@JsonProperty("ResponseStatusCode")
	private String responseStatusCode;
	
	@JsonProperty("ResponseStatusDesc")
	private String responseStatusDesc;


}

