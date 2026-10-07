package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetQuoteCountReq {
	
	@JsonProperty("CustomerId")
	private String customerId ;

	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("CreatedBy")
	private String createdBy ;
}
