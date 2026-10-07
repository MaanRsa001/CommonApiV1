package com.maan.eway.claim;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NonMotorRes {
	
	//Common Details
		@JsonProperty("PolicyDetails")
	    private NonMotorPolicyRes     nonMotorPolicyRes ;
		//BrokerDetails
		@JsonProperty("BrokerDetails")
	    private NonMotorBrokerRes     nonMotorBrokerRes ;
		//EndtFields
	    @JsonProperty("EndorsementDetails") 
	    private NonMotEndtRes     nonMotEndtRes ;   
		// Location Based
		@JsonProperty("LocationList")
		private List<NonMotorLocRes> locationList;

}
