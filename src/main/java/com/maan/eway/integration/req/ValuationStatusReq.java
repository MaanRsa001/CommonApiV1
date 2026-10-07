package com.maan.eway.integration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationStatusReq {
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("VehicleRegNo")
	private String vehicleRegNo;
	
	@JsonProperty("ValCompanyId")
	private String valCompanyId;
}
