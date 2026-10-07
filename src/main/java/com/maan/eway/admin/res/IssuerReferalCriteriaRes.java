package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuerReferalCriteriaRes {

	@JsonProperty("ReferalId")
	private Integer referalId ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("ReferalName")
	private String referalName ;
	
	@JsonProperty("OldReferalName")
	private String oldReferalName ;
	
	@JsonProperty("SumInsuredStart")
	private Double sumInsuredStart ;
	
	@JsonProperty("SumInsuredEnd")
	private Double sumInsuredEnd ;
	
	@JsonProperty("Status")
	private String status ;
	
	@JsonProperty("Remarks")
	private String remarks ;
	

}
