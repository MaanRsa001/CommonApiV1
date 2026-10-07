package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClausesMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;
    
	@JsonProperty("ClausesId")
	private String clausesId;
    
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
//	
//	@JsonProperty("PolicyType")
//	private String policyType;
}
