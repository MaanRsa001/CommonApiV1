package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortFolioDashBoardRes {

	   @JsonProperty("ProductId")
	   private String     productId;
	   
	   @JsonProperty("ProductName")
	   private String     productName;
	   
	   @JsonProperty("BrokerList")
	   private List<PortfolioBrokerListRes>     brokerList;
	   
	   @JsonProperty("BrokerCount")
	   private Long brokerCount;
	   
	   @JsonProperty("BrokerName")
	   private String brokerName;
	   
	   @JsonProperty("BranchName")
	   private String branchName;
	   
	
}
