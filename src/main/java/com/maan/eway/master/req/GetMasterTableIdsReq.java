package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetMasterTableIdsReq {
	
	@JsonProperty("Desc")
	private String desc;
	
	@JsonProperty("MasterType")
	private String masterType;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("CountryCode")
	private String countryCode;
	
	@JsonProperty("MakeId")
	private String makeId;
}
