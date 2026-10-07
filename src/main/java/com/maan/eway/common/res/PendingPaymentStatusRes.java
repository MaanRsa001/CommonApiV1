package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PendingPaymentStatusRes {

	@JsonProperty("PaymentStausRes")
	private List<PaymentStausRes> paymentStausRes;
	
	@JsonProperty("TotalCount")
	private String totalCount ;
	
}
