package com.maan.eway.admin.res;



import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerDepositCbcDetailsGetRes {

	// Deposit Cbc Details
	
	
	@JsonProperty("CbcNo")
	private String cbcNo;
	
	@JsonProperty("CustomerId")
    private String    customerId ;
	
	@JsonProperty("BroKerId")
	private String brokerId;
	
	@JsonProperty("BrokerName")
	private String brokerName;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("DepositAmount")
	private Double depositAmount;
	
	@JsonProperty("DepositUtilized")
	private Double depositUtilized;
	
	@JsonProperty("RefundAmount")
	private Double refundAmount;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("Policyrefundamount")
	private Double policyrefundamount;
	
	@JsonProperty("CompanyId")
	private String companyId;
}
