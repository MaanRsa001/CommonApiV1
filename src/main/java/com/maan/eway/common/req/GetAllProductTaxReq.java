package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetAllProductTaxReq {

	@JsonProperty("ProductId")
    private String     productId     ;

 	
 	@JsonProperty("CountryId")
    private String     countryId     ;
   
	@JsonProperty("TaxFor")
   private String     taxFor;
	
 	 @JsonProperty("InsuranceId")
 	 private String companyId;
 	 
 	 @JsonProperty("BranchCode")
 	 private String branchCode;
}
