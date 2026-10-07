package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetPolicyBySourceReq {
	@JsonProperty("DivisionCode")
	private String divisionCode;

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("SourceCode")
	private String sourceCode;
}
