package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetApproverListReq {
	
		@JsonProperty("BranchCode")
	   private String     branchCode ;

	   
	   @JsonProperty("ProductId")
	   private String     productId ;
	   
	   @JsonProperty("SumInsured")
	   private String     sumInsured ;
	   
	   @JsonProperty("CompanyId")
	   private String     companyId ;
	   
	
}
