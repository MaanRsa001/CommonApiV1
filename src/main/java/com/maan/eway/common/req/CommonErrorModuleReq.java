package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CommonErrorModuleReq {


	
	@JsonProperty("ModuleId")
	private String moduleId;
	
	@JsonProperty("ModuleName")
	private String moduleName;
	
	@JsonProperty("InsuraneId")
	private String insuranceId ;
	

	@JsonProperty("BranchCode")
	private String branchCode;
	

	@JsonProperty("ProductId")
	private String productId ;
	
	
	
}
