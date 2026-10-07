package com.maan.eway.creditAmount.Res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data



public class BrokerCreditDetailsRes {
	
	@JsonProperty("BrokerId")
	private String brokerId;

	@JsonProperty("DepositAmount")
	private String depositAmount;
	
	@JsonProperty("DepositUtilized")
	private String depositUtilized;
	
	@JsonProperty("CbcNo")
	private String cbcNo;
	
	@JsonProperty("BalanceAmount")
    private String balanceAmount;
	
	@JsonProperty("DepositNo")
    private String depositNo;

    @JsonProperty("PremiumAmount")
    private String premiumAmount;

}
