package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MotorCoverNoteRes {

	@JsonProperty("VehicleId")
	private String vehicleId;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("InsurerName")
	private String insurerName;
	
	@JsonProperty("PaymentDate")
	private String paymentDate;
	
	@JsonProperty("DateofIssue")
	private String dateofIssue;
	
	@JsonProperty("StartDate")
	private String startDate;
	
	@JsonProperty("EndDate")
	private String endDate;
	
	@JsonProperty("CovernoteNo")
	private String covernoteNo;
	
	@JsonProperty("StickerNumber")
	private String stickerNumber;
	
	@JsonProperty("RegistrationNumber")
	private String registrationNumber;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("VehicleTypeDesc")
	private String vehicleTypeDesc;
	
	@JsonProperty("ModelType")
	private String modelType;
	
	@JsonProperty("ColorDesc")
	private String colorDesc;
	
	@JsonProperty("CubicCapacity")
	private String cubicCapacity;
	
	@JsonProperty("VehicleMakeDesc")
	private String vehicleMakeDesc;
	
	@JsonProperty("ChassisNumber")
	private String chassisNumber;
	
	@JsonProperty("SeatingCapacity")
	private String seatingCapacity;
	
	@JsonProperty("EngineNumber")
	private String engineNumber;
	
	@JsonProperty("FuelType")
	private String fuelType;
	
	@JsonProperty("PolicyTypeDesc")
	private String policyTypeDesc;
	
	@JsonProperty("ManufactureYear")
	private String manufactureYear;
	
	@JsonProperty("AgentMobile")
	private String agentMobile;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("ModelNumber")
	private String modelNumber;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("VatPremium")
	private String vatPremium;
	
	@JsonProperty("OverallPremium")
	private String overallPremium;
	
	@JsonProperty("MotorUsageDesc")
	private String motorUsageDesc;
	
	@JsonProperty("sumInsured")
	private String sumInsured;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CompanyLogo")
	private String companyLogo;
	
	@JsonProperty("PremiumDetailsRes")
	private List<TaxInvoicePremiumDetails> premiumDetailsRes;

}
