package com.maan.eway.Rtsa.res;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetRtsaListRes {

	 	@JsonProperty("Registration_No")
	    private String     registrationNo ;
	    
	    @JsonProperty("MakeName")
	    private String makeName;
	    
	    @JsonProperty("ModelName")
	    private String modelName;
	    
	    @JsonProperty("EngineNo")
	    private String engineNo;
	    
	    @JsonProperty("ChassisNo")
	    private String chassisNo;
	    
	    @JsonProperty("YearMake")
	    private String yearMake;
	    
	    @JsonProperty("Gvm")
	    private String gvm;
	    
	    @JsonProperty("BodyType")
	    private String bodyType;
	    
	    @JsonProperty("Color")
	    private String color;
	    
	    @JsonProperty("NumberOfSeats")
	    private String numberOfSeats;
	    
	    @JsonProperty("FirstRegDate")
	    private String firstRegDate;
	    
	    @JsonProperty("CurrentLinenseExpDate")
	    private String currentLinenseExpDate;
	    
	    @JsonProperty("RoadWortExpDate")
	    private String roadWortExpDate;
	    
	    @JsonProperty("RegStatus")
	    private String regStatus;
	
	
}
