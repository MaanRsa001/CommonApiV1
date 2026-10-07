package com.maan.eway.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RiskDetailsGetRes {

	  @JsonProperty("CustomerReferenceNo")
	    private String   customerReferenceNo ;
	    
	    @JsonProperty("RequestReferenceNo")
	    private String   requestReferenceNo ;
		
	    @JsonProperty("CdRefNo")
	    private String   cdRefNo    ;
	    @JsonProperty("VdRefNo")
	    private String     vdRefNo;
	    @JsonProperty("MsRefNo")
	    private String     msRefNo;
		@JsonProperty("Idnumber")
	    private String     idNumber     ;
		@JsonProperty("Vehicleid")
	    private Integer    vehicleId    ;
	    private String     accident     ;
		@JsonProperty("Gpstrackinginstalled")
	    private String     gpsTrackingInstalled ;
		@JsonProperty("Windscreencoverrequired")
	    private String     windScreenCoverRequired ;
		@JsonProperty("Insurancetype")
	    private String     insuranceType ;
		@JsonProperty("InsuranceTypeDesc")
	    private String     insuranceTypeDesc;
		@JsonProperty("MotorCategory")
	    private String     motorCategory ;
		@JsonProperty("MotorCategoryDesc")
	    private String     motorCategoryDesc ;
		@JsonProperty("Motorusage")
	    private String     motorUsage   ;
		@JsonProperty("Registrationnumber")
	    private String     registrationNumber ;
		@JsonProperty("Chassisnumber")
	    private String     chassisNumber ;
		@JsonProperty("Vehiclemake")
	    private String     vehicleMake  ;
		@JsonProperty("VehiclemakeDesc")
	    private String     vehicleMakeDesc  ;
		@JsonProperty("Vehcilemodel")
	    private String     vehcileModel ;
		@JsonProperty("VehcilemodelDesc")
	    private String     vehcileModelDesc ;
		@JsonProperty("VehicleType")
	    private String     vehicleType  ;
		@JsonProperty("VehicleTypeDesc")
	    private String     vehicleTypeDesc  ;
		@JsonProperty("ModelNumber")
	    private String     modelNumber  ;
		@JsonProperty("EngineNumber")
	    private String     engineNumber ;
		@JsonProperty("FuelType")
	    private String     fuelType     ;
		@JsonProperty("FuelTypeDesc")
	    private String     fuelTypeDesc     ;
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("RegistrationYear")
	    private Date   registrationYear ;
		@JsonProperty("SeatingCapacity")
	    private Integer    seatingCapacity ;
		@JsonProperty("CubicCapacity")
	    private Double     cubicCapacity ;
		@JsonProperty("Color")
	    private String     color        ;
		@JsonProperty("ColorDesc")
	    private String     colorDesc ;
		@JsonProperty("Grossweight")
	    private Double     grossWeight  ;
		@JsonProperty("Tareweight")
	    private Double     tareWeight   ;
		@JsonProperty("Actualpremium")
	    private Double     actualPremium ;
		@JsonProperty("CoverNoteNo")
	    private String     covernoteNo  ;
		@JsonProperty("Stickerno")
	    private String     stickerNo    ;
	    private String     periodOfInsurance ;
		@JsonProperty("WindScreenSumInsured")
	    private Double     windScreenSumInsured ;
		@JsonProperty("AcccessoriesSumInsured")
	    private Double     acccessoriesSumInsured ;
		@JsonProperty("AccessoriesInformation")
	    private String     accessoriesInformation ;
		@JsonProperty("NumberOfAxels")
	    private String    numberOfAxels ;
		@JsonProperty("AxelDistance")
	    private Double     axelDistance ;
		@JsonProperty("SumInsured")
	    private Double     sumInsured   ;
		@JsonProperty("OverRidePercentage")
	    private Double    overridePercentage   ;
		@JsonProperty("TppdFreeLimit")
	    private Double     tppdFreeLimit ;
		@JsonProperty("TppdIncreaeLimit")
	    private Double     tppdIncreaeLimit ;
		@JsonProperty("InsurerSettlement")
	    private Double     insurerSettlement ;
		@JsonProperty("PolicyType")
	    private String     policyType   ;
		@JsonProperty("PolicyTypeDesc")
	    private String     policyTypeDesc   ;	
		@JsonProperty("RadioOrCasseteplayer")
	    private String     radioorcasseteplayer ;
		@JsonProperty("RoofRack")
	    private Double     roofRack     ;
		@JsonProperty("SpotFogLamp")   
		private Double     spotFogLamp  ;
		@JsonProperty("TrailerDetails")
	    private String     trailerDetails ;
		@JsonProperty("Drivenby")
	    private String     drivenBy     ;
		@JsonProperty("DrivenByDesc")
	    private String     drivenByDesc     ;
		@JsonProperty("VehicleInterestedCompany")
	    private String     vehicleInterestedCompany ;
		@JsonProperty("InterestedCompanyDetails")
	    private String     interestedCompanyDetails ;
		@JsonProperty("OtherVehicle")
	    private String     otherVehicle ;
		@JsonProperty("OtherVehicleDetails")
	    private String     otherVehicleDetails ;
		@JsonProperty("OtherInsurance")
	    private String     otherInsurance ;
		@JsonProperty("OtherInsuranceDetails")
	    private String     otherInsuranceDetails ;
		@JsonProperty("HoldInsurancePolicy")
	    private String     holdInsurancePolicy ;
		@JsonProperty("NoOfClaims")
	    private Integer    noOfClaims   ;
		@JsonProperty("AdditionalCircumstances")
	    private String     additionalCircumstances ;
		@JsonProperty("BranchCode")
	    private String     branchCode ;
		@JsonProperty("AgencyCode")
	    private String     agencyCode ;
		@JsonProperty("SectionId")
	    private String    sectionId ;
		@JsonProperty("ProductId")
	    private String  productId ;
		@JsonProperty("InsuranceId")
	    private String  companyId ;
		@JsonProperty("InsuranceClass")
	    private String  insuranceClass ;
		@JsonProperty("OwnerCategory")
	    private String ownerCategory;
		@JsonProperty("ManufactureAge")
	    private Integer manufactureAge;
		@JsonProperty("RegistrationAge")
	    private Integer registrationAge;
		@JsonProperty("NcdYears")   
	    private Integer ncdYears;
		@JsonProperty("NcdYn")
	    private String    ncdYn ;	



		@JsonProperty("ManufactureYear")
	    private String manufactureYear;

		@JsonProperty("Status")
	    private String   status;

		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("UpdatedDate")
	    private Date updatedDate;

		@JsonProperty("UpdatedBy")
	    private String  updatedBy;

		@JsonProperty("CreatedBy")
	    private String  createdBy;
		
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("PolicyStartDate")
	    private Date policyStartDate;

		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("PolicyEndDate")
	    private Date policyEndDate;
		
		@JsonProperty("Currency")
	    private String  currency;
		
		@JsonProperty("ExchangeRate")
	    private String  exchangeRate;
		
		@JsonProperty("CollateralYn")
	    private String  collateralYn;
		
		@JsonProperty("BorrowerType")
	    private String  borrowerType;
		
		@JsonProperty("CollateralName")
	    private String collateralName;
		
		@JsonProperty("FirstLossPayee")
	    private String firstLossPayee;
		
		@JsonProperty("FleetOwnerYn")
	    private String fleetOwnerYn;
		
		@JsonProperty("NoOfVehicles")
	    private String noOfVehicles;
		
		@JsonProperty("NoOfComprehensives")
	    private String noOfComprehensives;
		
		@JsonProperty("ClaimRatio")
	    private String claimRatio;

		@JsonProperty("CityLimit")
		private String     cityLimit;
		
		@JsonProperty("SavedFrom")
	    private String   savedFrom;
		
		@JsonProperty("ActualPremiumLc")
		private String actualPremiumLc;
		
		@JsonProperty("AcctualPremiumFc")
		private String actualPremiumFc ;
		
		@JsonProperty("OverallPremiumLc")
		private String overallPremiumLc ;
		
		@JsonProperty("OverallPremiumFc")
		private String    overallPremiumFc ;
		
		@JsonProperty("BrokerCode")
		private String brokerCode;
		
		@JsonProperty("LoginId")
		private String loginId;
		
		@JsonProperty("AcExecutiveId")
		private String acExecutiveId;
		
		@JsonProperty("SubUserType")
		private String subUserType;
		
		@JsonProperty("ApplicationId")
		private String applicationId;
		
	@JsonProperty("PersonId")
    private String    personId   ;

	@JsonProperty("InbuildConstructType")
    private String     inbuildConstructType ;
	@JsonProperty("BuildingFloors")
    private String buildingFloors ;
	@JsonProperty("OutbuildConstructType")
    private String     outbuildConstructType ;
	@JsonProperty("BuildingUsageYn")
    private String     buildingUsageYn ;
	@JsonProperty("BuildingPurpose")
    private String     buildingPurpose;
	@JsonProperty("BuildingUsageDesc")
    private String     buildingUsageDesc ;
	@JsonProperty("BuildingPurposeId")
    private String     buildingPurposeId;
	@JsonProperty("BuildingUsageId")
    private String     buildingUsageId;

	@JsonProperty("PaDeathSuminsured")
    private String     paDeathSuminsured ;
	

	@JsonProperty("PaPermanentdisablementSuminsured")
    private String     paPermanentdisablementSuminsured ;
	
	@JsonProperty("PaTotaldisabilitySumInsured")
    private String     paTotaldisabilitySumInsured ;
	
	@JsonProperty("PaMedicalSuminsured")
    private String     paMedicalSuminsured ;
	
	@JsonProperty("BuildingType")
	private String     buildingType;

	@JsonProperty("BuildingOwnerYn")
	private String     buildingOwnerYn;
	@JsonProperty("PersonalIntermediarySuminsured")
    private String     personalIntermediarySuminsured ;
	
	
	@JsonProperty("BuildingOccupationType")
    private String     buildingOccupationType ;
	@JsonProperty("WithoutInhabitantDays")
    private String    withoutInhabitantDays ;

	@JsonProperty("BuildingCondition")
    private String     buildingCondition ;
	@JsonProperty("BuildingBuildYear")
    private String    buildingBuildYear ;
	
	@JsonProperty("BuidingAreaSqm")
    private String     buidingAreaSqm ;
	@JsonProperty("BuildingSuminsured")
    private String     buildingSuminsured ;
	@JsonProperty("AllriskSumInsured")
    private String     allriskSuminsured ;
	@JsonProperty("ContentSuminsured")
    private String     contentSuminsured ;
	
	@JsonProperty("OccupationType")
    private String    occupationType;

	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("DomesticPackageYn")
    private String    domesticPackageYn;

	@JsonProperty("CategoryId")
    private String    categoryId;

	 @JsonProperty("QuoteNo")
	    private String     quoteNo ;
	    
	    @JsonProperty("PassengerId")
	    private String     passengerId;
	    @JsonProperty("TravelId")
	    private Integer    travelId     ;
		
		@JsonProperty("GenderId")
	    private String    genderId     ;
	
		@JsonProperty("PassengerName")
	    private String     passengerName ;
		
		@JsonProperty("PassengerFirstName")
	    private String     passengerFirstName ;
		@JsonProperty("PassengerLastName")
	    private String     passengerLastName ;

		@JsonFormat(pattern = "dd/MM/yyyy")
		@JsonProperty("Dob")
	    private Date       dob          ;
		@JsonProperty("GenderDesc")
	    private String     genderDesc   ;
		@JsonProperty("Age")
	    private Integer    age          ;
		@JsonProperty("RelationId")
	    private Integer    relationId   ;
		@JsonProperty("RelationDesc")
	    private String     relationDesc ;
		@JsonProperty("Nationality")
	    private String     nationality  ;
		@JsonProperty("NationalityDesc")
	    private String     nationalityDesc  ;
		@JsonProperty("CoverType")
	    private String     coverType    ;
		@JsonProperty("PassportNo")
	    private String     passportNo   ;
		@JsonProperty("CivilId")
	    private String     civilId      ;
//		@JsonProperty("TotalPremium")
//	    private Double     totalPremium ;
		@JsonFormat(pattern = "dd/MM/yyyy")
		@JsonProperty("EntryDate")
	    private Date       entryDate    ;
	@JsonProperty("TravelCoverId")
	    private Integer    travelCoverId ;
		@JsonProperty("CompanyName")
	    private String     companyName  ;
		@JsonProperty("ProductName")
	    private String     productName  ;
		@JsonProperty("SectionName")
	    private String     sectionName  ;
		@JsonProperty("TravelCoverDesc")
	    private String     travelCoverDesc ;
		@JsonProperty("SourceCountry")
	    private String     sourceCountry ;
		@JsonProperty("SourceCountryDesc")
	    private String     sourceCountryDesc ;
		@JsonProperty("DestinationCountry")
	    private String     destinationCountry ;
		@JsonProperty("DestinationCountryDesc")
	    private String     destinationCountryDesc ;
		@JsonProperty("SportsCoverYn")
	    private String     sportsCoverYn ;
		@JsonProperty("TerrorismCoverYn")
	    private String     terrorismCoverYn ;
		@JsonProperty("PlanTypeId")
	    private Integer    planTypeId   ;
		@JsonProperty("PlanTypeDesc")
	    private String     planTypeDesc ;
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("TravelStartDate")
	    private Date       travelStartDate ;
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("TravelEnddDate")
	    private Date       travelEndDate ;
		@JsonProperty("TravelCoverDuration")
	    private Integer    travelCoverDuration ;
		@JsonProperty("TotalPassengers")
	    private Integer    totalPassengers ;
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("EffectiveDate")
	    private Date       effectiveDate ;
		@JsonProperty("Remarks")
	    private String     remarks      ;
		@JsonProperty("HavePromoCode")
	    private String     havepromocode ;
		@JsonProperty("PromoCode")
	    private String     promocode    ;
		@JsonProperty("CovidCoverYn")
	    private String     covidCoverYn ;
		@JsonProperty("CustomerId")
	    private String     customerId   ;
		@JsonProperty("AdminLoginId")
	    private String     adminLoginId ;
		@JsonProperty("AdminRemarks")
	    private String     adminRemarks ;
		@JsonProperty("RejectReason")
	    private String     rejectReason ;
		@JsonProperty("ReferalRemarks")
	    private String     referalRemarks ;
		@JsonProperty("BdmCode")
	    private String     bdmCode      ;
		@JsonProperty("SourceType")
	    private String     sourceType   ;
		@JsonProperty("CustomerCode")
	    private String     customerCode ;
		@JsonProperty("BrokerBranchCode")
	    private String     brokerBranchCode ;
		@JsonProperty("BrokerBranchName")
	    private String     brokerBranchName ;
		@JsonProperty("CommissionType")
	    private String     commissionType ;
		@JsonProperty("CommissionTypeDesc")
	    private String     commissionTypeDesc ;
//		@JsonProperty("StateCode")
//	    private String     stateCode;
//		@JsonProperty("StateName")
//	    private String     stateName     ;
		

		@JsonProperty("GroupId")
	    private String   groupId;
		@JsonProperty("GroupCount")
	    private String   groupCount;
		
		@JsonProperty("RiskId")
	    private String    riskId   ;
		
		@JsonProperty("PolicyPeriod")
	    private String     policyPeriod   ;
		@JsonProperty("SalaryPerAnnum")
	    private String     salaryPerAnnum ;
		
		@JsonProperty("BenefitCoverMonth")
	    private String     benefitCoverMonth;
		
		@JsonProperty("CustomerName")
	    private String    customerName;
		
		
		@JsonProperty("JobJoiningMonth")
	    private String    jobJoiningMonth;
		
		@JsonProperty("BetweenDiscontinued")
	    private String    betweenDiscontinued;
		
		@JsonProperty("EthicalWorkInvolved")
	    private String    ethicalWorkInvolved;
	
		@JsonProperty("LocationId")
	    private String    locationId   ;
		
}
