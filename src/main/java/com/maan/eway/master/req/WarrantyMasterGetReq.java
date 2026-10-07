package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WarrantyMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("WarrantyId")
    private String    warrantyId     ;
    
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("ProductId")
	private String productId;
	
//	@JsonProperty("PolicyType")
//	private String policyType;
//	
}
