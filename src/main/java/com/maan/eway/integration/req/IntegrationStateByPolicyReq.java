package com.maan.eway.integration.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IntegrationStateByPolicyReq {
private static final long serialVersionUID = 1L;
	@JsonProperty("SearchType")
	private String searchType;
	@JsonProperty("StartDate")
	private Date startDate;
	@JsonProperty("EndDate")
	private Date endDate;
	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("PolicyNo")
	private String policyNo;
}
