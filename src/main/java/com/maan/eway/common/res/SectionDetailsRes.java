package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.CoverRes;

import lombok.Data;

@Data
public class SectionDetailsRes {

	@JsonProperty("RiskId")
	private String riskId;

	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("SectionName")
	private String sectionName;

	@JsonProperty("Count")
	private String count;
	
	@JsonProperty("GroupDesc")
	private  String groupDesc;
	
	@JsonProperty("GroupId")
	private  String groupId;
	@JsonProperty("PassengerId")
	private  String passengerId;	
	
	@JsonProperty("PassengerName")
	private  String passengerName;

	@JsonProperty("OccupationId")
	private String occupationId;

	@JsonProperty("OccupationDesc")
	private String occupationDesc;

	@JsonProperty("SumInsured")
	private String sumInsured;

	@JsonProperty("ContentType")
	private String contentType;

	@JsonProperty("ContentDesc")
	private String contentDesc;

	@JsonProperty("LocationId")
	private String locationId;

	@JsonProperty("LocationName")
	private String locationName;

	@JsonProperty("PremiumAfterDiscount")
	private String premiumAfterDiscount;

	@JsonProperty("PremiumAfterDiscountLc")
	private String premiumAfterDiscountLc;

	@JsonProperty("PremiumBeforeDiscount")
	private String premiumBeforeDiscount;

	@JsonProperty("PremiumBeforeDiscountLc")
	private String premiumBeforeDiscountLc;

	@JsonProperty("PremiumExcluedTax")
	private String premiumExcluedTax;

	@JsonProperty("PremiumExcluedTaxLc")
	private String premiumExcluedTaxLc;

	@JsonProperty("PremiumIncludedTax")
	private String premiumIncludedTax;

	@JsonProperty("PremiumIncludedTaxLc")
	private String premiumIncludedTaxLc;

	@JsonProperty("Covers")
	private List<CoverRes> covers;

	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;

	@JsonProperty("MoneySafeLimit")
	private String moneySafeLimit;

	@JsonProperty("MoneyOutofSafe")
	private String moneyOutofSafe;

	@JsonProperty("MoneyDirectorResidence")
	private String moneyDirectorResidence;

	@JsonProperty("MoneyCollector")
	private String moneyCollector;

	@JsonProperty("MoneyAnnualEstimate")
	private String moneyAnnualEstimate;

	@JsonProperty("MoneyMajorLoss")
	private String moneyMajorLoss;

	@JsonProperty("PremiumLc")
	private Double premiumLc;

	@JsonProperty("PremiumFc")
	private Double premiumFc;

	@JsonProperty("OverAllPremiumFc")
	private Double overAllPremiumFc;

	@JsonProperty("OverAllPremiumLc")
	private Double overAllPremiumLc;

	@JsonProperty("CommissionAmount")
	private String commissionAmount;

	@JsonProperty("CommissionPercentage")
	private String commissionPercentage;

	@JsonProperty("VatCommission")
	private String vatCommission;

	@JsonProperty("FinalyzeYn")
	private String finalizeYn;

	@JsonProperty("OccupationTypeDesc")
	private String occupationTypeDesc;
	
	//Motor Fields

	@JsonProperty("Accident")
	private String accident;
	@JsonProperty("Gpstrackinginstalled")
	private String gpsTrackingInstalled;
	@JsonProperty("Windscreencoverrequired")
	private String windScreenCoverRequired;
	@JsonProperty("Insurancetype")
	private String insuranceType;
	@JsonProperty("InsuranceTypeDesc")
	private String insuranceTypeDesc;
	@JsonProperty("MotorCategory")
	private String motorCategory;
	@JsonProperty("MotorCategoryDesc")
	private String motorCategoryDesc;
	@JsonProperty("Motorusage")
	private String motorUsage;

	@JsonProperty("Registrationnumber")
	private String registrationNumber;
	@JsonProperty("Chassisnumber")
	private String chassisNumber;
	@JsonProperty("Vehiclemake")
	private String vehicleMake;
	@JsonProperty("VehiclemakeDesc")
	private String vehicleMakeDesc;
	@JsonProperty("Vehcilemodel")
	private String vehcileModel;
	@JsonProperty("VehcilemodelDesc")
	private String vehcileModelDesc;
	@JsonProperty("VehicleType")
	private String vehicleType;
	@JsonProperty("VehicleTypeDesc")
	private String vehicleTypeDesc;
	@JsonProperty("ModelNumber")
	private String modelNumber;
	@JsonProperty("EngineNumber")
	private String engineNumber;
	@JsonProperty("FuelType")
	private String fuelType;
	@JsonProperty("FuelTypeDesc")
	private String fuelTypeDesc;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("RegistrationYear")
	private Date registrationYear;
	@JsonProperty("SeatingCapacity")
	private Integer seatingCapacity;
	@JsonProperty("CubicCapacity")
	private Double cubicCapacity;
	@JsonProperty("Color")
	private String color;
	@JsonProperty("ColorDesc")
	private String colorDesc;
	@JsonProperty("Grossweight")
	private Double grossWeight;
	@JsonProperty("Tareweight")
	private Double tareWeight;
	@JsonProperty("Actualpremium")
	private Double actualPremium;
	@JsonProperty("CoverNoteNo")
	private String covernoteNo;
	@JsonProperty("Stickerno")
	private String stickerNo;
	@JsonProperty("WindScreenSumInsured")
	private Double windScreenSumInsured;
	@JsonProperty("AcccessoriesSumInsured")
	private Double acccessoriesSumInsured;
	@JsonProperty("AccessoriesInformation")
	private String accessoriesInformation;
	@JsonProperty("NumberOfAxels")
	private String numberOfAxels;
	@JsonProperty("AxelDistance")
	private Double axelDistance;
	@JsonProperty("OverRidePercentage")
	private Double overridePercentage;
	@JsonProperty("TppdFreeLimit")
	private Double tppdFreeLimit;
	@JsonProperty("TppdIncreaeLimit")
	private Double tppdIncreaeLimit;
	@JsonProperty("InsurerSettlement")
	private Double insurerSettlement;
	@JsonProperty("PolicyType")
	private String policyType;
	@JsonProperty("PolicyTypeDesc")
	private String policyTypeDesc;
	@JsonProperty("RadioOrCasseteplayer")
	private String radioorcasseteplayer;
	@JsonProperty("RoofRack")
	private Double roofRack;
	@JsonProperty("SpotFogLamp")
	private Double spotFogLamp;
	@JsonProperty("TrailerDetails")
	private String trailerDetails;

	@JsonProperty("InsuranceClass")
	private String insuranceClass;
	@JsonProperty("OwnerCategory")
	private String ownerCategory;
	@JsonProperty("ManufactureAge")
	private Integer manufactureAge;
	@JsonProperty("RegistrationAge")
	private Integer registrationAge;
	@JsonProperty("NcdYears")
	private Integer ncdYears;
	@JsonProperty("NcdYn")
	private String ncdYn;

	@JsonProperty("ManufactureYear")
	private String manufactureYear;

	@JsonProperty("CollateralYn")
	private String collateralYn;

	@JsonProperty("BorrowerType")
	private String borrowerType;

	@JsonProperty("CollateralName")
	private String collateralName;

	@JsonProperty("FirstLossPayee")
	private String firstLossPayee;

	@JsonProperty("FleetOwnerYn")
	private String fleetOwnerYn;

	@JsonProperty("NoOfComprehensives")
	private String noOfComprehensives;

	@JsonProperty("ClaimRatio")
	private String claimRatio;

	@JsonProperty("CityLimit")
	private String cityLimit;

	@JsonProperty("DocumentsTitle")
	private String documentsTitle;

	@JsonProperty("SavedFrom")
	private String savedFrom;

	@JsonProperty("DriverDetails")
	private List<DriverDetailsRes> driverDetails;

	@JsonProperty("EndorsementYn")
	private String endorsementYn;

	@JsonProperty("EndtCount")
	private BigDecimal endtCount;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;

	@JsonProperty("BorrowerTypeDesc")
	private String borrowerTypeDesc;

	@JsonProperty("BankCode")
	private String bankCode;

	@JsonProperty("BankName")
	private String bankName;
	
	@JsonProperty("WallType")
	private String wallType;
	
	@JsonProperty("WallTypeDesc")
	private String wallTypeDesc;

	@JsonProperty("RoofType")
	private String roofType;
	
	@JsonProperty("RoofTypeDesc")
	private String roofTypeDesc;
	
	@JsonProperty("CoverId")
	private Integer coverid;
}
