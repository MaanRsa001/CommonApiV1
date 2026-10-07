package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchRes {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("PolicyNo")
	private String policyNo;

	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;

	@JsonProperty("CustomerName")
	private String clientName;

	@JsonProperty("BranchName")
	private String branchName;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("MobileNumber")
	private String mobileNo1;

	@JsonProperty("PolicyType")
	private String policyTypeDesc;

	@JsonProperty("VehicleType")
	private String vehicleTypeDesc;

//	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private String policyStartDate;

//	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private String policyEndDate;

	@JsonProperty("OverallPremiumLc")
	private String overallPremiumLc;

//	@JsonFormat(pattern = "dd/MM/yyyy")

	@JsonProperty("QuoteDate")
	private String entryDate;

	@JsonProperty("Currency")
	private String currency;

	@JsonProperty("ExchangeRate")
	private String exchangeRate;

	@JsonProperty("GpsTrackingInstalled")
	private String gpsTrackingInstalled;

	@JsonProperty("WindScreenCoverRequired")
	private String windScreenCoverRequired;

	@JsonProperty("NoOfClaims")
	private String noOfClaims;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;

	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("EmiPremium")
	private String emiPremium;
	@JsonProperty("EmiYn")
	private String emiYn;
	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;

	@JsonProperty("NoOfInstallment")
	private String noOfInstallment;

	/*
	 * 
	 * @JsonProperty("BrokerBranchCode") private String brokerBranchCode ;
	 * 
	 * @JsonProperty("Idnumber") private String idNumber ;
	 * 
	 * @JsonProperty("Vehicleid") private Integer vehicleId ; private String
	 * accident ;
	 * 
	 * @JsonProperty("Windscreencoverrequired") private String
	 * windScreenCoverRequired ;
	 * 
	 * @JsonProperty("Insurancetype") private String insuranceType ;
	 * 
	 * @JsonProperty("InsuranceTypeDesc") private String insuranceTypeDesc;
	 * 
	 * @JsonProperty("Registrationnumber") private String registrationNumber ;
	 * 
	 * @JsonProperty("Chassisnumber") private String chassisNumber ;
	 * 
	 * @JsonProperty("Vehiclemake") private String vehicleMake ;
	 * 
	 * @JsonProperty("HavePromoCode") private String havepromocode ;
	 * 
	 * 
	 * 
	 * @JsonProperty("AdminRemarks") private String adminRemarks;
	 * 
	 * @JsonProperty("ReferalRemarks") private String referalRemarks ;
	 * 
	 * @JsonProperty("PromoCode") private String promocode ;
	 * 
	 * @JsonProperty("BankCode") private String bankCode;
	 * 
	 * @JsonProperty("VehiclemakeDesc") private String vehicleMakeDesc ;
	 * 
	 * @JsonProperty("Vehcilemodel") private String vehcileModel ;
	 * 
	 * @JsonProperty("VehcilemodelDesc") private String vehcileModelDesc ;
	 * 
	 * @JsonProperty("VehicleTypeDesc") private String vehicleTypeDesc ;
	 * 
	 * @JsonProperty("ModelNumber") private String modelNumber ;
	 * 
	 * @JsonProperty("SumInsured") private Double sumInsured ;
	 * 
	 * @JsonProperty("DrivenByDesc") private String drivenByDesc ;
	 * 
	 * @JsonProperty("BranchCode") private String branchCode ;
	 * 
	 * @JsonProperty("AgencyCode") private String agencyCode ;
	 * 
	 * @JsonProperty("SectionId") private String sectionId ;
	 * 
	 * @JsonProperty("ProductId") private String productId ;
	 * 
	 * @JsonProperty("InsuranceId") private String companyId ;
	 * 
	 * @JsonProperty("InsuranceClass") private String insuranceClass ;
	 * 
	 * 
	 * @JsonFormat(pattern="dd/MM/yyyy")
	 * 
	 * @JsonProperty("ManufactureYear") private Date manufactureYear;
	 * 
	 * 
	 * 
	 * @JsonFormat(pattern="dd/MM/yyyy")
	 * 
	 * @JsonProperty("UpdatedDate") private Date updatedDate;
	 * 
	 * @JsonProperty("UpdatedBy") private String updatedBy;
	 * 
	 * @JsonProperty("CreatedBy") private String createdBy;
	 * 
	 * 
	 * 
	 * @JsonProperty("SavedFrom") private String savedFrom;
	 * 
	 * 
	 * 
	 * 
	 * @JsonProperty("BrokerCode") private String brokerCode;
	 * 
	 * 
	 * 
	 * @JsonProperty("AcExecutiveId") private String acExecutiveId;
	 * 
	 * @JsonProperty("SubUserType") private String subUserType;
	 * 
	 * @JsonProperty("ApplicationId") private String applicationId;
	 * 
	 * 
	 * @JsonProperty("CustomerId") private String customerId;
	 * 
	 * @JsonProperty("BdmCode") private String bdmCode;
	 * 
	 * @JsonProperty("SourceType") private String sourceType;
	 * 
	 * @JsonProperty("CustomerCode") private String customerCode;
	 * 
	 * 
	 * @JsonProperty("CommissionType") private String commissionType;
	 * 
	 * @JsonProperty("EndorsementType") private String endorsementType;
	 * 
	 * @JsonProperty("EndorsementTypeDesc") private String endorsementTypeDesc;
	 * 
	 * 
	 * 
	 * @JsonFormat(pattern="dd/MM/yyyy")
	 * 
	 * @JsonProperty("EndorsementDate") private Date endorsementDate ;
	 * 
	 * @JsonProperty("EndorsmentRemarks") private String endorsementRemarks ;
	 * 
	 * @JsonProperty("OrginalPolicyNo") private String originalPolicyNo ;
	 * 
	 * @JsonProperty("EndtPrevPolicyNo") private String endtPrevPolicyNo ;
	 * 
	 * @JsonProperty("EndtPrevQuoteNo") private String endtPrevQuoteNo ;
	 * 
	 * @JsonProperty("EndtCount") private BigDecimal endtCount ;
	 * 
	 * @JsonProperty("EndtStatus") private String endtStatus ;
	 * 
	 * 
	 * @JsonProperty("IsFinanceYesNo") private String isFinaceYn ;
	 * 
	 * 
	 * @JsonProperty("EndtCategDesc") private String endtCategDesc ;
	 * 
	 * //old
	 * 
	 * 
	 * 
	 * @JsonProperty("IdsCount") private String idsCount ;
	 * 
	 * @JsonProperty("OldRequestReferenceNo") private String oldRequestReferenceNo ;
	 * 
	 * @JsonProperty("SectionName") private String sectionName ;
	 * 
	 * // //TAVEL // // @JsonProperty("TravelId") // private String travelId ; //
	 * // @JsonProperty("ProductName") // private String productName ; //
	 * // @JsonProperty("SectionName") // private String sectionName ;
	 * // @JsonProperty("CompanyName") // private String companyName ; //
	 * // @JsonProperty("TravelCoverId") // private String travelCoverId ;
	 * // @JsonProperty("SourceCountry") // private String sourceCountry ;
	 * // @JsonProperty("DestinationCountry") // private String destinationCountry ;
	 * // @JsonProperty("SportsCoverYn") // private String sportsCoverYn ;
	 * // @JsonProperty("TerrorismCoverYn") // private String terrorismCoverYn ;
	 * // @JsonProperty("PlanTypeId") // private String planTypeId ; //
	 * // @JsonFormat(pattern="dd/MM/yyyy") // @JsonProperty("TravelStartDate") //
	 * private Date travelStartDate ; // // @JsonFormat(pattern="dd/MM/yyyy")
	 * // @JsonProperty("TravelEndDate") // private Date travelEndDate ; //
	 * // @JsonProperty("TravelCoverDuration") // private String travelCoverDuration
	 * ; // @JsonProperty("TotalPassengers") // private String totalPassengers ; //
	 * // @JsonProperty("Age") // private String age ; //
	 * // @JsonProperty("Remarks") // private String remarks ; //
	 * // @JsonProperty("HavePromoCode") // private String havepromocode ; //
	 * // @JsonProperty("PromoCode") // private String promocode ;
	 * // @JsonProperty("CovidCoverYn") // private String covidCoverYn ; //
	 * // @JsonProperty("AdminLoginId") // private String adminLoginId ; //
	 * // @JsonProperty("AdminRemarks") // private String adminRemarks ;
	 * // @JsonProperty("RejectReason") // private String rejectReason ; //
	 * // @JsonFormat(pattern="dd/MM/yyyy") // @JsonProperty("EntryDate") // private
	 * Date entryDate ; // // @JsonProperty("SourceCountryDesc") // private String
	 * sourceCountryDesc; // // @JsonProperty("DestinationCountryDesc") // private
	 * String desctinationCountryDesc; // //// @JsonProperty("GroupDetails") ////
	 * private List<TravelGroupGetRes> groupDetails;
	 */
}
