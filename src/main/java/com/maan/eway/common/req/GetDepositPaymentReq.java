package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetDepositPaymentReq {
	
	@JsonProperty("CbcNo")
	private String CbcNo;
	
	@JsonProperty("DepositNo")
	private String depositNo;

}
