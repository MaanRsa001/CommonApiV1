package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TravelRiskDetails {

	@JsonProperty("CustomerReferenceNo")
    private String   customerReferenceNo ;
    
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
	

}
