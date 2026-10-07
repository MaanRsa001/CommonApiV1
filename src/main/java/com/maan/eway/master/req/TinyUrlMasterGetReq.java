package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TinyUrlMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
		
	@JsonProperty("BranchCode")
	private String branchCode;
		
	@JsonProperty("Sno")
	private String sno;
	
}
