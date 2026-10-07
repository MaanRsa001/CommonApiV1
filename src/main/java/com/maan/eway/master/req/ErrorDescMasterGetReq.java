package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrorDescMasterGetReq implements Serializable{
	
   private static final long serialVersionUID = 1L;
	
	@JsonProperty("ErrorCode")
	private String errorCode;
	
	@JsonProperty("ModuleId")
	private String moduleId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("BranchCode")
	private String branchCode;
}
