package com.maan.eway.endorsment.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class EndDeleteReq {
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("CompanyId")
	private String companyid;
	
}
