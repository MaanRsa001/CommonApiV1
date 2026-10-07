package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateQuoteStatusReq {


	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("RejectReason")
	private String rejectReason;
	
}
