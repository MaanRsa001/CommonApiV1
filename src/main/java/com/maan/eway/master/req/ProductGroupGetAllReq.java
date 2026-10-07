package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductGroupGetAllReq {
	
	  @JsonProperty("ProductId")
	    private Integer    productId ;

	  @JsonProperty("BranchCode")
	    private String     branchCode ;

	  @JsonProperty("CompanyId")
	    private String     companyId ;

	

}