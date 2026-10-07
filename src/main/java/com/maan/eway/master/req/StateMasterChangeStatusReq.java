package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StateMasterChangeStatusReq {
	 
	 @JsonProperty("StateId")
	 private String stateId;
	 
	 @JsonProperty("CountryId")
	 private String countryId;
	 
	 @JsonProperty("Status")
	 private String status;
	
}
