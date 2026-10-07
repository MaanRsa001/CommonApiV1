package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerLocationReq {
	 @JsonProperty("LocationId")
	    private Long locationId;
 
	    @JsonProperty("CustomerReferenceNo")
	    private String customerReferenceNo;
}