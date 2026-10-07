package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDetailsReq {

	@JsonProperty("QuotationPolicyNo")
	private String quotationPolicyNo;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("EndtNo")
	private String endtNo;

	@JsonProperty("RegionCode")
	private String regionCode;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("ChassisNo")
	private String chassisno;

}
