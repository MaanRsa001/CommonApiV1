package com.maan.eway.integration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationListReq {
	
	@JsonProperty("SearchBy")
	private String searchBy;
	
	@JsonProperty("SearchValue")
	private String searchValue;
	
	@JsonProperty("StartDate")
	private String startDate;
	
	@JsonProperty("EndDate")
	private String endDate;
	
	@JsonProperty("CompanyId")
	private String companyId;
}
