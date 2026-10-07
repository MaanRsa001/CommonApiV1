package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class PaymentCashRes {
	
	@JsonProperty("PayeeName")
	 private String payeeName;
   

}
