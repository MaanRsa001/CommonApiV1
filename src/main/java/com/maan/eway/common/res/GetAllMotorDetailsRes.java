package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetAllMotorDetailsRes {


	    @JsonProperty("CustomerReferenceNo")
	    private String   customerReferenceNo ;
	    @JsonProperty("BrokerBranchCode")
	    private String     brokerBranchCode ;
	    @JsonProperty("RequestReferenceNo")
	    private String   requestReferenceNo ;
		@JsonProperty("Idnumber")
	    private String     idNumber     ;
		@JsonProperty("Vehicleid")
	    private Integer    vehicleId    ;
	    private String     accident     ;
		@JsonProperty("Windscreencoverrequired")
	    private String     windScreenCoverRequired ;
		@JsonProperty("Insurancetype")
	    private String     insuranceType ;
		@JsonProperty("InsuranceTypeDesc")
	    private String     insuranceTypeDesc;
		@JsonProperty("Registrationnumber")
	    private String     registrationNumber ;
		@JsonProperty("Chassisnumber")
	    private String     chassisNumber ;
		@JsonProperty("Vehiclemake")
	    private String     vehicleMake  ;
		@JsonProperty("HavePromoCode")
	    private String     havepromocode ;
		
		@JsonProperty("PolicyTypeDesc")
	    private String     policyTypeDesc;
		
		@JsonProperty("AdminRemarks")
	    private String     adminRemarks;
		
		@JsonProperty("ReferalRemarks")
	    private String     referalRemarks ;
		
		@JsonProperty("PromoCode")
	    private String     promocode    ;
		@JsonProperty("BankCode")
	    private String    bankCode;
		
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
		@JsonProperty("SumInsured")
	    private String     sumInsured   ;
		@JsonProperty("DrivenByDesc")
	    private String     drivenByDesc     ;
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
		
		@JsonProperty("EntryDate")
	    private String       entryDate ;
		
	//	@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("ManufactureYear")
	    private String manufactureYear;

		@JsonProperty("Status")
	    private String   status;

	//	@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("UpdatedDate")
	    private String updatedDate;

		@JsonProperty("UpdatedBy")
	    private String  updatedBy;

		@JsonProperty("CreatedBy")
	    private String  createdBy;
		
	//	@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("PolicyStartDate")
	    private String policyStartDate;

	//	@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("PolicyEndDate")
	    private String policyEndDate;
		
		@JsonProperty("SavedFrom")
	    private String  savedFrom;

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
		
		@JsonProperty("Currency")
	    private String  currency;
		
		@JsonProperty("ExchangeRate")
	    private String  exchangeRate;
		
		@JsonProperty("QuoteNo")
		private String quoteNo;
		  
		@JsonProperty("CustomerId")
		private String customerId;
		
		@JsonProperty("BdmCode")
		private String bdmCode;
		
		@JsonProperty("SourceType")
		private String sourceType;
		
		@JsonProperty("CustomerCode")
		private String customerCode;
		
		
		@JsonProperty("CommissionType")
		private String commissionType;
		
		@JsonProperty("EndorsementType")
	    private String endorsementType;
		
		@JsonProperty("EndorsementTypeDesc")
	    private String endorsementTypeDesc;
		
		

	////	@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("EndorsementDate")
	    private String       endorsementDate ;

		@JsonProperty("EndorsmentRemarks")
	    private String     endorsementRemarks ;

	    @JsonFormat(pattern="dd/MM/yyyy")
	    @JsonProperty("EndorsementEffectiveDate")
	    private String       endorsementEffdate ;
	    
	    @JsonProperty("PolicyNo")
	    private String   policyNo ;


	    @JsonProperty("OrginalPolicyNo")
	    private String     originalPolicyNo ;

	    @JsonProperty("EndtPrevPolicyNo")
	    private String     endtPrevPolicyNo ;

	    @JsonProperty("EndtPrevQuoteNo")
	    private String     endtPrevQuoteNo ;

	    @JsonProperty("EndtCount")
	    private String endtCount ;

	    @JsonProperty("EndtStatus")
	    private String  endtStatus ;
	       
	    
	    @JsonProperty("IsFinanceYesNo")
	    private String isFinaceYn ;
	    
	    
	    @JsonProperty("EndtCategDesc")
	    private String     endtCategDesc ;
		
//old
	    
	    @JsonProperty("ClientName")
	    private String   clientName ;
	    
	    @JsonProperty("IdsCount")
	    private String   idsCount ;
	    
	    @JsonProperty("OldRequestReferenceNo")
	    private String   oldRequestReferenceNo ;
	    
		@JsonProperty("SectionName")
	    private String     sectionName    ;
		
//		//TAVEL
//	  
//	    @JsonProperty("TravelId")
//	    private String    travelId     ;
//	   
//		@JsonProperty("ProductName")
//	    private String     productName    ;
//
//		@JsonProperty("SectionName")
//	    private String     sectionName    ;
//		@JsonProperty("CompanyName")
//	    private String    companyName    ;
//
//		@JsonProperty("TravelCoverId")
//	    private String    travelCoverId ;
//		@JsonProperty("SourceCountry")
//	    private String     sourceCountry ;
//		@JsonProperty("DestinationCountry")
//	    private String     destinationCountry ;
//		@JsonProperty("SportsCoverYn")
//	    private String     sportsCoverYn ;
//		@JsonProperty("TerrorismCoverYn")
//	    private String     terrorismCoverYn ;
//		@JsonProperty("PlanTypeId")
//	    private String    planTypeId   ;
//		
//	//	@JsonFormat(pattern="dd/MM/yyyy")
//		@JsonProperty("TravelStartDate")
//	    private Date       travelStartDate ;
//		
//	//	@JsonFormat(pattern="dd/MM/yyyy")
//		@JsonProperty("TravelEndDate")
//	    private Date       travelEndDate ;
//		
//		@JsonProperty("TravelCoverDuration")
//	    private String    travelCoverDuration ;
//		@JsonProperty("TotalPassengers")
//	    private String    totalPassengers ;
//		
//		@JsonProperty("Age")
//	    private String    age          ;
//		
//		@JsonProperty("Remarks")
//	    private String     remarks      ;
//		
//		@JsonProperty("HavePromoCode")
//	    private String     havepromocode ;
//		
//		@JsonProperty("PromoCode")
//	    private String     promocode    ;
//		@JsonProperty("CovidCoverYn")
//	    private String     covidCoverYn ;
//		
//		@JsonProperty("AdminLoginId")
//	    private String     adminLoginId ;
//		
//		@JsonProperty("AdminRemarks")
//	    private String     adminRemarks ;
//		@JsonProperty("RejectReason")
//	    private String     rejectReason ;
//	
//	//	@JsonFormat(pattern="dd/MM/yyyy")
//		@JsonProperty("EntryDate")
//	    private Date       entryDate ;
//		
//		@JsonProperty("SourceCountryDesc")
//		private String sourceCountryDesc;
//		
//		@JsonProperty("DestinationCountryDesc")
//		private String desctinationCountryDesc;
//
////		private List<TravelGroupGetRes> groupDetails;
}
