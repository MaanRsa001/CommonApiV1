package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetPaymentStatusRes {

	@JsonProperty("PaymentStausRes")
	private List<PaymentStausRes> paymentStausRes;
	
	@JsonProperty("TotalCount")
	private Long totalCount ;
	
//	@JsonProperty("PendingPaymentStatus")
//	private List<PendingPaymentStatusRes> pendingPaymentStatusRes;
//	
//	@JsonProperty("FailedPaymentStatus")
//	private List<PendingPaymentStatusRes> pailedPaymentStatusRes;
//	
//	@JsonProperty("SuccessPaymentStatus")
//	private List<PendingPaymentStatusRes> successPaymentStatusRes;
	
}
