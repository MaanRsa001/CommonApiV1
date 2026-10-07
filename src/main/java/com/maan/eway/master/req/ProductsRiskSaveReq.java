package com.maan.eway.master.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductsRiskSaveReq {

	@JsonProperty("ProductId")
    private String     productId ;
	
	@JsonProperty("ProductName")
    private String     productName ;
	
	@JsonProperty("CustomerId")
    private String     customerId ;
	
	@JsonProperty("RequestReferenceNo")
    private String     requestReferenceNo ;
	
	@JsonProperty("InsuranceId")
    private String     insuranceId ;
	
	@JsonProperty("BranchCode")
    private String     branchCode;
	
	@JsonProperty("CreatedBy")
    private String     createdBy;
	
	@JsonProperty("ProductRiskList")
    private List<ProductSectionsSaveReq>     productRiskList ;
    
}
