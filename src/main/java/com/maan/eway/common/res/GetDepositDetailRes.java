package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetDepositDetailRes {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("PremiumAmt")
	private String premiumAmt;
	
	@JsonProperty("EntryDate")
	private String entryDate;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CbcNo")
	private String cbcNo;
	
	@JsonProperty("DepositNo")
	private String depositNo;
	
	@JsonProperty("BalanceAmt")
	private String balanceAmt;
	
	@JsonProperty("ReceiptNo")
	private String receiptNo;
	
	@JsonProperty("BrokerId")
	private String brokerId;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("PolicyInsuranceFee")
	private String policyInsuranceFee;
	
	@JsonProperty("VatAmount")
	private String vatAmount;
	
	@JsonProperty("ChargableType")
	private String chargableType;
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("DepositType")
	private String depositType;

}
