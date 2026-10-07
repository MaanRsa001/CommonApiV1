package com.maan.eway.claim;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MotorRes  {


	@JsonProperty("Quotationpolicyno")
   private String     quotationPolicyNo ;
	@JsonProperty("Endtno")
   private BigDecimal endtNo       ;
	@JsonProperty("Requestreferenceno")
   private String     requestreferenceno ;
	@JsonProperty("Indexno")
   private String     indexNo      ;
	@JsonProperty("Iterationno")
   private BigDecimal iterationNo  ;
	@JsonProperty("Insuredname")
   private String     insuredName  ;
	@JsonProperty("Insureddriversdob")
   private Date       insuredDriversDob ;
	@JsonProperty("Dateof1stregistration")
   private Date       dateOf1stRegistration ;
	@JsonProperty("Drivinglicenceissuedon")
   private Date       drivingLicenceIssuedOn ;
	@JsonProperty("Mulkiyatodate")
   private Date       mulkiyaToDate ;
	@JsonProperty("Mulkiyafromdate")
   private Date       mulkiyaFromDate ;
	@JsonProperty("Couponcodehm")
   private String     couponCodeHm ;
	@JsonProperty("Registrationformno")
   private String     registrationFormNo ;
	@JsonProperty("Orangecardno")
   private String     orangeCardNo ;
	@JsonProperty("Sectiondata")
   private String     sectionData  ;
	@JsonProperty("Couponcodepa")
   private String     couponCodePa ;
	@JsonProperty("Couponcodetravel")
   private String     couponCodeTravel ;
	@JsonProperty("Offroadcover")
   private BigDecimal offRoadCover ;
	@JsonProperty("Amountclaimedbefore")
   private BigDecimal amountClaimedBefore ;
	@JsonProperty("Yearofmanufacture")
   private BigDecimal yearOfManufacture ;
	@JsonProperty("Noofclaims")
   private BigDecimal noOfClaims   ;
	@JsonProperty("Aaaroadsideassistanceno")
   private String     aaaRoadsideAssistanceNo ;
	@JsonProperty("Currency")
   private String     currency     ;
	@JsonProperty("Sectionplus")
   private BigDecimal sectionPlus  ;
	@JsonProperty("Importvehicleyn")
   private String     importVehicleyn ;
	@JsonProperty("Orangecardyn")
   private String     orangeCardYn ;
	@JsonProperty("Seats")
   private String seats        ;
	@JsonProperty("Vehicletonnagecc")
   private BigDecimal vehicleTonnageCc ;
	@JsonProperty("Vehicletonnageccstr")
   private String vehicleTonnageCcStr ;
	@JsonProperty("Orangecardbookid")
   private BigDecimal orangeCardBookId ;
	@JsonProperty("Geographicalextension")
   private String     geographicalExtension ;
	@JsonProperty("Vehiclemakemodel")
   private String     vehicleMakeModel ;
	@JsonProperty("Profession")
   private String     profession   ;
	@JsonProperty("Countryofmake")
   private BigDecimal countryOfMake ;
	@JsonProperty("Vehicletype")
   private BigDecimal vehicleType  ;
	@JsonProperty("Vehicletypestr")
   private String vehicleTypeStr  ;
	@JsonProperty("Orangecardtype")
   private String     orangeCardType ;
	@JsonProperty("Agencyrepair")
   private BigDecimal agencyRepair ;
	@JsonProperty("Platenocharacter")
   private String     plateNoCharacter ;
	@JsonProperty("ChassisNo")
   private String     chassisNo    ;
	@JsonProperty("Engineno")
   private String     engineNo     ;
	@JsonProperty("Importorigin")
   private String     importOrigin ;
	@JsonProperty("Vehiclecategory")
   private String     vehicleCategory ;
	@JsonProperty("Ncd")
   private BigDecimal ncd          ;
	@JsonProperty("Pwserror")
   private String     pWsError     ;
	@JsonProperty("Responsetime")
   private Date       responseTime ;
	@JsonProperty("Status")
   private String     status       ;
	@JsonProperty("Remarks")
   private String     remarks      ;
	@JsonProperty("Pwsresponsetype")
   private String     pWsResponseType ;
	@JsonProperty("Suminsured")
   private String     suminsured ;
	@JsonProperty("Vehicletypedesc")
   private String     vehicletypedesc ;
	@JsonProperty("Vehiclemodeldesc")
   private String     vehiclemodeldesc ;
	@JsonProperty("Praihsifc")
	private String praihsifc;
	@JsonProperty("Praihpremfc")
	private String praihpremfc;
	@JsonProperty("Vehtype")
	private String vehtype;
	@JsonProperty("Vehmake")
	private String vehmake;
	@JsonProperty("Vehmodel")
	private String vehmodel;
	@JsonProperty("Vehbodytype")
	private String vehbodytype;
	@JsonProperty("Manufactureyear")
	private String manufactureyear;
	@JsonProperty("Vehiclecc")
	private String vehiclecc;
	@JsonProperty("Seating")
	private String seating;
	@JsonProperty("Tonnage")
	private String tonnage;
	@JsonProperty("Tiraprodcode")
	private String tiraprodcode;
	@JsonProperty("Tiraprodcodedesc")
	private String tiraprodcodedesc;
	@JsonProperty("VechRegNo")
	private String vechregno;
	
     
	  
	  
}
