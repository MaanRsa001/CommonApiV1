package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelDataSetOneRes {

	@JsonProperty("PassengerName")
	private String passengerName;
	
	@JsonProperty("Dob")
	private String dob;
	
	@JsonProperty("Age")
	private String age;
	
	@JsonProperty("RelationDesc")
	private String relationDesc;
	
	@JsonProperty("PassportNo")
	private String passportNo;
	
	@JsonProperty("TravelCoverDuration")
	private String travelCoverDuration;
	
	@JsonProperty("Sno")
	private String sno;
	
}
