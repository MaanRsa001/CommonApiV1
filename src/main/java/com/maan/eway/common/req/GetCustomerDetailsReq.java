package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetCustomerDetailsReq {

	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo ;
	
	@JsonProperty("InsuredReferenceNo")
    private String insuredReferenceNo ;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
}
