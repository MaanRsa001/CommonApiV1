package com.maan.eway.ussd.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UssdApiReq {

	@JsonProperty("RequestId")
	private String requestId;
	
	@JsonProperty("CoverNoteType")
	private String coverNoteType;
	
	@JsonProperty("CoverNoteNumber")
	private String coverNoteNumber;
	
	@JsonProperty("PrevCoverNoteReferenceNumber")
	private String prevCoverNoteReferenceNumber;
	
	@JsonProperty("CoverNoteStartDate")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private String coverNoteStartDate;
	
	@JsonProperty("CoverNoteEndDate")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private String coverNoteEndDate;
	
	@JsonProperty("CoverNoteDesc")
	private String coverNoteDesc;
	
	@JsonProperty("OperativeClause")
	private String operativeClause;
	
	@JsonProperty("PaymentMode")
	private String paymentMode;
	
	@JsonProperty("CurrencyCode")
	private String currencyCode;
	
	@JsonProperty("ExchangeRate")
	private String exchangeRate;
	
	@JsonProperty("TotalPremiumExcludingTax")
	private String totalPremiumExcludingTax;
	
	@JsonProperty("TotalPremiumIncludingTax")
	private String totalPremiumIncludingTax;
	
	@JsonProperty("CommissionPaid")
	private String commissionPaid;
	
	@JsonProperty("CommissionRate")
	private String commissionRate;
	
	@JsonProperty("OfficerName")
	private String officerName;
	
	@JsonProperty("OfficerTitle")
	private String officerTitle;
	
	@JsonProperty("ProductCode")
	private String productCode;
	
	@JsonProperty("EndorsementType")
	private String endorsementType;
	
	@JsonProperty("EndorsementReason")
	private String endorsementReason;
	
	@JsonProperty("EndorsementPremiumEarned")
	private String endorsementPremiumEarned;
	
	@JsonProperty("RiskCode")
	private String riskCode;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("SumInsuredEquivalent")
	private String sumInsuredEquivalent;
	
	@JsonProperty("PremiumRate")
	private String premiumRate;
	
	@JsonProperty("PremiumBeforeDiscount")
	private String premiumBeforeDiscount;
	
	@JsonProperty("PremiumAfterDiscount")
	private String premiumAfterDiscount;
	
	@JsonProperty("PremiumExcludingTaxEquivalent")
	private String premiumExcludingTaxEquivalent;
	
	@JsonProperty("PremiumIncludingTax")
	private String premiumIncludingTax;
	
	@JsonProperty("DiscountType")
	private String discountType;
	
	@JsonProperty("DiscountRate")
	private String discountRate;
	
	@JsonProperty("DiscountAmount")
	private String discountAmount;
	
	@JsonProperty("TaxCode")
	private String taxCode;
	
	@JsonProperty("IsTaxExempted")
	private String isTaxExempted;
	
	@JsonProperty("TaxExemptionType")
	private String taxExemptionType;
	
	@JsonProperty("TaxExemptionReference")
	private String taxExemptionReference;
	
	@JsonProperty("TaxRate")
	private String taxRate;
	
	@JsonProperty("TaxAmount")
	private String taxAmount;
	
	@JsonProperty("SubjectMatterReference")
	private String subjectMatterReference;
	
	@JsonProperty("SubjectMatterDesc")
	private String subjectMatterDesc;
	
	@JsonProperty("PolicyHolderName")
	private String policyHolderName;
	
	@JsonProperty("PolicyHolderDateOfBirth")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private String policyHoldeDateOfBirth;
	
	@JsonProperty("PolicyHolderType")
	private String policyHolderType;
	
	@JsonProperty("PolicyHolderNumber")
	private String policyHolderNumber;
	
	@JsonProperty("PolicyHolderIdType")
	private String policyHolderIdType;
	
	@JsonProperty("Gender")
	private String gender;
	
	@JsonProperty("CountryCode")
	private String countryCode;
	
	@JsonProperty("CityCode")
	private String cityCode;
	
	@JsonProperty("Region")
	private String region;
	
	@JsonProperty("District")
	private String district;
	
	@JsonProperty("Street")
	private String street;
	
	@JsonProperty("PolicyHolderPhoneNumber")
	private String policyHolderPhoneNumber;
	
	@JsonProperty("PolicyHolderFax")
	private String policyHolderFax;
	
	@JsonProperty("PostalAddress")
	private String postalAddress;
	
	@JsonProperty("EmailAddress")
	private String emailAddress;
	
	@JsonProperty("MotorCategory")
	private String motorCategory;
	
	@JsonProperty("MotorType")
	private String motorType;
	
	@JsonProperty("RegistrationNumber")
	private String registrationNumber;
	
	@JsonProperty("ChassisNumber")
	private String chassisNumber;
	
	@JsonProperty("Make")
	private String make;
	
	@JsonProperty("Model")
	private String model;
	
	@JsonProperty("ModelNumber")
	private String modelNumber;
	
	@JsonProperty("BodyType")
	private String bodyType;
	
	@JsonProperty("Color")
	private String color;
	
	@JsonProperty("EngineNumber")
	private String engineNumber;
	
	@JsonProperty("EngineCapacity")
	private String engineCapacity;
	
	@JsonProperty("FuelUsed")
	private String fuelUsed;
	
	@JsonProperty("NumberOfAxles")
	private String numberOfAxles;
	
	@JsonProperty("AxleDistance")
	private String axleDistance;
	
	@JsonProperty("SittingCapacity")
	private String sittingCapacity;
	
	@JsonProperty("YearOfManufacture")
	private String yearOfManufacture;
	
	@JsonProperty("TareWeight")
	private String tareWeight;
	
	@JsonProperty("GrossWeight")
	private String grossWeight;
	
	@JsonProperty("MotorUsage")
	private String motorUsage;
	
	@JsonProperty("OwnerName")
	private String ownerName;
	
	@JsonProperty("OwnerCategory")
	private String ownerCategory;
	
	@JsonProperty("OwnerAddress")
	private String ownerAddress;
	
	@JsonProperty("CoverNoteReferenceNumber")
	private String coverNoteReferenceNumber;
	
	@JsonProperty("StickerNumber")
	private String stickerNumber;
	
	@JsonProperty("TransactionId")
	private String transactionId;
	
	@JsonProperty("TransactionAmount")
	private String transactionAmount;
}
