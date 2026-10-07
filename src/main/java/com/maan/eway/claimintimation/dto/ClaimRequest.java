package com.maan.eway.claimintimation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClaimRequest {
	
	@JsonProperty("QuotationPolicyNo")
	private String quotationPolicyNo;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
}
