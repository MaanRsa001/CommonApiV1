package com.maan.eway.yara.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YaraReq {
	
	@JsonProperty("CustomerDetails")
	private YaraCustomerDetails customerDetails;
	
	@JsonProperty("PolicyDetails")
	private YaraCustomerPolicyDetails policyDetails;
	
	@JsonProperty("RiskDetails")
	private YaraRiskDetails riskDetails;

}
