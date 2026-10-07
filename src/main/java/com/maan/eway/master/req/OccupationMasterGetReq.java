package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OccupationMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("OccupationId")
    private String    occupationId     ;
    
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
}
