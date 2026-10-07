package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BankMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("BankCode")
    private String     bankCode     ;
    
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
}
