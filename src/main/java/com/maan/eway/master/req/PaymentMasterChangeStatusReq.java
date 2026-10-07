package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentMasterChangeStatusReq {

	@JsonProperty("PaymentMasterId")
	private String paymentMasterId;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
}
