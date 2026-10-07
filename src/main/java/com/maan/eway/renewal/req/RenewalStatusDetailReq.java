package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalStatusDetailReq {

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("TranId")
	private String tranId;
	
	@JsonProperty("StatusCode")
	private String statusCode;

}
