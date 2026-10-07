package com.maan.eway.renewal.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewPremiaPolicyRes {

	@JsonProperty("TransactionId")
	private String transactionId;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("DivisionCode")
    private String divisionCode;
	
	@JsonProperty("DivisionName")
	private String divisionName;
	
	@JsonProperty("PolprodCode")
	private String polProdCode;
	
	
	@JsonProperty("VehicleUsageLocal")
	private String vehicleUsageLocal;
	
	@JsonProperty("ProdName")
	private String prodName;
	
	@JsonProperty("PolType")
	private String polType;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("PolAssrCode")
	private String polAssrCode;
	
	@JsonProperty("PolAssrName")
	private String polAssrName;
	
	@JsonProperty("SourceCode")
	private String sourceCode;
	
	@JsonProperty("SourceName")
	private String sourceName;
	
	@JsonProperty("PolNo")
	private String polNo;
	
	@JsonProperty("PolSysId")
	private String polSysId;
	
	@JsonProperty("IndexNo")
	private String indexNo;
	
	@JsonProperty("PolFmDt")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date polFmDt;
	
	@JsonProperty("PolExpDt")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date polExpDt;
	
	@JsonProperty("NewStartDate")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date newStartDate;
	
	@JsonProperty("PolPrem")
	private Double polPrem;
	
	@JsonProperty("PolSiLc1")
	private Double polSiLc1;
	
	@JsonProperty("NetPrem")
	private Double netPrem;
	
	@JsonProperty("ChargeAmt")
	private Double chargeAmt;
	
	@JsonProperty("LoadingPremium")
	private Double loadingPremium;
	
	@JsonProperty("DiscountPremium")
	private Double discountPremium;
	
	@JsonProperty("InsuredCivilId")
	private String insuredCivilId;
	
	@JsonProperty("MobileCode")
	private String mobileCode;
	
	@JsonProperty("InsuredMobile")
	private String insuredMobile;
	
	@JsonProperty("InsuredEmailId")
	private String insuredEmailId;
	
	@JsonProperty("MakeId")
	private String makeId;
	
	@JsonProperty("MakeIdLocal")
	private String makeIdLocal;
	
	@JsonProperty("MakeIdName")
	private String makeIdName;
	
	@JsonProperty("ModelId")
	private String modelId;
	
	@JsonProperty("ModelIdName")
	private String modelIdName;
	
	@JsonProperty("BodyType")
	private String bodyType;
	
	@JsonProperty("BodyTypeLocal")
	private String bodyTypeLocal;
	
	@JsonProperty("BodyTypeName")
	private String bodyTypeName;
	
	@JsonProperty("PlateNumber")
	private String plateNumber;
	
	@JsonProperty("ChassNo")
	private String chassNo;
	
	@JsonProperty("EngineNumber")
	private String engineNumber;
	
	@JsonProperty("ManufactureYear")
	private String manufactureYear;
	
	@JsonProperty("TypeOfCover")
	private String typeOfCover;
	
	@JsonProperty("TypeOfCoverLocal")
	private String typeOfCoverLocal;
	
	@JsonProperty("TypeOfCoverName")
	private String typeOfCoverName;
	
	@JsonProperty("UsageType")
	private String usageType;
	
	@JsonProperty("UsageTypeName")
	private String usageTypeName;
	
	@JsonProperty("VehicleAge")
	private String vehicleAge;
	
	@JsonProperty("NoOfpassenger")
	private String noOfPassenger;
	
	@JsonProperty("Seating")
	private String seating;
	
	@JsonProperty("Cc")
	private String cc;
	
	@JsonProperty("claimFreeYears")
	private String claimFreeYears;
	
	@JsonProperty("RenewalCount")
	private String renewalCount;
	
	@JsonProperty("Color")
	private String color;
	
	@JsonProperty("ColorName")
	private String colorName;
	
	@JsonProperty("PlateColor")
	private String plateColor;
	
	@JsonProperty("PlateColorName")
	private String plateColorName;
	
	@JsonProperty("VehicleValue")
	private Double vehicleValue;
	
	@JsonProperty("Tonnage")
	private String tonnage;
	
	@JsonProperty("RequestTime")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date requestTime;
	
	@JsonProperty("ResponseTime")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date responseTime;
	
	@JsonProperty("EntryDate")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date entryDate;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("DriverName")
    private String driverName;
    
	@JsonProperty("DriverLicense")
    private String driverLicense;
    
	@JsonProperty("DriverDob")
    private Date driverDob;
    
	@JsonProperty("DriverAge")
    private String driverAge;
    
	@JsonProperty("DriverGender")
    private String driverGender;
    
	@JsonProperty("DriverNationality")
    private String driverNationality;
    
	@JsonProperty("DriverLicIssueDate")
    private Date driverLicIssueDate;
    
	@JsonProperty("DriverLicExpireDate")
    private Date driverLicExpireDate;
    
	@JsonProperty("DriverLicType")
    private String driverLicType;
    
	@JsonProperty("DriverIssuedAt")
    private String driverIssuedAt;
    
	@JsonProperty("ClaimExp")
    private String claimExp;
	
	
	
}
