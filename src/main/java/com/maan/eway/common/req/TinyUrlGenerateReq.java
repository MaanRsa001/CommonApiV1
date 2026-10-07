package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TinyUrlGenerateReq {

	@JsonProperty("Param")
	private String param;
	@JsonProperty("Type")
	private String type;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("InsuranceId")
	private String companyId;
	@JsonProperty("BranchCode")
	private String branchCode;
	
}
