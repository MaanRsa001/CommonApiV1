package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class RenewalVehicleReq {
	
	@JsonProperty("PolicyNo")
	private String policyNo;

	@JsonProperty("RiskId")	    
	private String riskId;

	@JsonProperty("Make")	    
	private String make;

	@JsonProperty("Model")	    
	private String model;

	@JsonProperty("BodyType")	    
	private String bodyType;

	@JsonProperty("VehicleUsage")	    
	private String vehicleUsage;

	@JsonProperty("PolicyType")	    
	private String policyType;

	@JsonProperty("SumInsured")	   
	private String sumInsured;

	@JsonProperty("CreatedBy")	    
	private String createdBy;

}
