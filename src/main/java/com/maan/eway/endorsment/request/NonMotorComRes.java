package com.maan.eway.endorsment.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NonMotorComRes {

	@JsonProperty("RequestReferenceNo")
    private String     requestReferenceNo ;
	
	@JsonProperty("EndorsementDetails") 
	private NonMotEndtReq     nonMotEndtReq ; 
}
