package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetByCustomerRefNoReq {

	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo ;
	@JsonProperty("PolCustCode")
	private String polCustCode;
	
}
