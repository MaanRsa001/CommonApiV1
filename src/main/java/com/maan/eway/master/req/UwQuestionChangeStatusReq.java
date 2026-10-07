package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UwQuestionChangeStatusReq {

	 @JsonProperty("UwQuestionId")
	 private String uwQuestionId;
	 
	 @JsonProperty("ProductId")
	 private String productId;
	 @JsonProperty("Status")
		private String status;
		
		@JsonProperty("InsuranceId")
		private String companyId;
		
		@JsonProperty("BranchCode")
		private String branchCode;
}
