package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.PassengerSectionDetails;

import lombok.Data;

@Data
public class EserviceTravelGetRes {

		@JsonProperty("RiskId")
	 	private  String riskId;	
	    
		@JsonProperty("TravelId")
	    private String    travelId     ;
		
		@JsonProperty("TravelCoverId")
	    private String    travelCoverId ;
		@JsonProperty("SourceCountry")
	    private String     sourceCountry ;
		@JsonProperty("DestinationCountry")
	    private String     destinationCountry ;
		@JsonProperty("SportsCoverYn")
	    private String     sportsCoverYn ;
		@JsonProperty("TerrorismCoverYn")
	    private String     terrorismCoverYn ;
		@JsonProperty("PlanTypeId")
	    private String    planTypeId   ;
		
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("TravelStartDate")
	    private Date       travelStartDate ;
		
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("TravelEndDate")
	    private Date       travelEndDate ;
		
		@JsonProperty("TravelCoverDuration")
	    private String    travelCoverDuration ;
		@JsonProperty("TotalPassengers")
	    private String    totalPassengers ;
		
		@JsonProperty("Age")
	    private String    age          ;
		
		@JsonProperty("CovidCoverYn")
	    private String     covidCoverYn ;
		
		
		@JsonProperty("SourceCountryDesc")
		private String sourceCountryDesc;
		
		@JsonProperty("DestinationCountryDesc")
		private String desctinationCountryDesc;
		
		@JsonProperty("EndorsementYn")
	    private String     endorsementYn;
		
		
		@JsonProperty("SectionId")
		private  String sectionId;	
		
		@JsonProperty("SectionName")
		private  String sectionName;	
		
		@JsonProperty("PassengerId")
		private  String passengerId;	
		
		@JsonProperty("PassengerName")
		private  String passengerName;	
		
		 @JsonProperty("SectionDetails")
		 private List<PassengerSectionDetails>    sectionDetails;

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
		
		@JsonProperty("PolicyNo")
	    private String policyNo;
		
}
