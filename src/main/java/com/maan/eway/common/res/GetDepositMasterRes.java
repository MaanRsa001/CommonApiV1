package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetDepositMasterRes {
	
	@JsonProperty("CbcNo")
	private String CbcNo;
	
	@JsonProperty("BrokerId")
	private String brokerId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("DepositAmount")
	private String depositAmount;
	
	@JsonProperty("DepositUtilised")
	private String depositUtilised;
	
	@JsonProperty("RefundAmt")
	private String refundAmt;
	
	@JsonProperty("PolicyRefundAmt")
	private String policyRefundAmt;
	
	@JsonProperty("BrokerName")
	private String brokerName;;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
}
