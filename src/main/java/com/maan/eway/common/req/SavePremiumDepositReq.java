package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavePremiumDepositReq {

	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("BrokerId")
	private String brokerId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Policyfee")
	private String policyfee;
	
	@JsonProperty("Vattaxamt")
	private String vattaxamt;
	
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
	@JsonProperty("PayeeName")
	private String payeeName;
	
}
