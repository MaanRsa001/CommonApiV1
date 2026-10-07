package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MotorPrivateVehicleDetails {

	@JsonProperty("VehicleId")
	private String vehicleId;
	
	@JsonProperty("RegistrationNumber")
	private String registrationNumber;
	
	@JsonProperty("VehicleMake")
	private String vehicleMake;
	
	@JsonProperty("VehcileModel")
	private String vehcileModel;
	
	@JsonProperty("VehicleTypeDesc")
	private String vehicleTypeDesc;
	
	@JsonProperty("CubicCapacity")
	private String cubicCapacity;
	
	@JsonProperty("ManufactureYear")
	private String manufactureYear;
	
	@JsonProperty("SeatingCapacity")
	private String seatingCapacity;
	
	@JsonProperty("ColorDesc")
	private String colorDesc;
	
	@JsonProperty("PolicyTypeDesc")
	private String policyTypeDesc;
	
	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
	@JsonProperty("WindScreenSumInsuredLc")
	private String windScreenSumInsuredLc;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("StickerNumber")
	private String stickerNumber;
	
	@JsonProperty("GrossWeight")
	private String grossWeight;
	
	@JsonProperty("InsTypeDesc")
	private String insTypeDesc;
	
	@JsonProperty("EngineNumber")
	private String engineNumber;
	
	@JsonProperty("ChassisNumber")
	private String chassisNumber;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("InceptionDate")
	private String inceptionDate;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
//	@JsonProperty("CustomerName")
//	private String customerName;
//	
//	@JsonProperty("PolicyNo")
//	private String policyNo;
//	
//	@JsonProperty("CompanyName")
//	private String companyName;
	
	@JsonProperty("TPPDIncreaseLimit")
	private String tPPDIncreaseLimit;
	
	@JsonProperty("FuelType")
	private String fuelType;
	
	@JsonProperty("applicableDiscounts")
	private String applicableDiscounts;
	
	@JsonProperty("ThirdPartyLiabilityLimit")
	private String thirdPartyLiabilityLimit;
	@JsonProperty("tracker")
	private String  tracker;
	@JsonProperty("ncb")
	private String  ncb;
	@JsonProperty("voluntary")
	private String  voluntary;
	
	@JsonProperty("GeographicArea")
	private String geographicArea;
	
}
