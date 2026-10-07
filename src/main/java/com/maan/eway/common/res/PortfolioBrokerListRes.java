package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortfolioBrokerListRes {

	   @JsonProperty("BrokerName")
	   private String     brokerName;
	   
	   @JsonProperty("BrokerCode")
	   private String     brokerCode;
	   
	   @JsonProperty("BrokerLoginId")
	   private String     brokerLoginId;
	   
	   @JsonProperty("UserType")
	   private String     userType;
	   
	   @JsonProperty("SubUserType")
	   private String     subUserType;
	   
	   @JsonProperty("TotalCount")
	   private Long     totalCount;
	    
	   @JsonProperty("TotalPremiumLc")
	   private String     totalPremiumLc;
	   
	   @JsonProperty("TotalPremiumFc")
	   private String     totalPremiumFc;
	   
	   @JsonProperty("SourceType")
	   private String     sourceType;
	   
	   @JsonProperty("BdmCode")
	   private String     bdmCode;
	   
	   @JsonProperty("ProductName")
	   private String     productName;
}
