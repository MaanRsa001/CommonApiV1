package com.maan.eway.integration.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationQuoteDetailsRes {
		
		@JsonProperty("QuoteNo")
	    private String     quoteNo ;

		@JsonProperty("CompanyId")
	    private String     companyId ;

		@JsonProperty("ProductId")
	    private Integer   productId ;

		@JsonProperty("VehicleId")
	    private String   vehicleId ;
	    
		@JsonProperty("VehicleRegNo")
	    private String   vehicleRegNo ;
	    
		@JsonProperty("FirstName")
	    private String   firstName;
	    
		@JsonProperty("Email")
	    private String   email;
	    
		@JsonProperty("CustomerMobile")
	    private String   customerMobile;
	    
		@JsonProperty("PolicyNo")
	    private String     policyNo ;
		
		@JsonProperty("BranchCode")
	    private String     branchCode ;
		
		@JsonProperty("LoginId")
	    private String     loginId ;
		
		@JsonProperty("SumInsured")
	    private Double     sumInsured ;
		
		@JsonProperty("BrokerName")
	    private String   brokerName;
	    
		@JsonProperty("BrokerMail")
	    private String   brokerMail;
		
		@JsonProperty("BrokerMobileNo")
	    private String   brokerMobileNo;

		
		

}
