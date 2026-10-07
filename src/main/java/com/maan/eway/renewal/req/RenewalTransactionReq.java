package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTransactionReq {

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("StartDate")
	private String startDate;
	
	@JsonProperty("EndDate")
	private String endDate;
	

}
