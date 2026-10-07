package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FollowupDetailsGetReq {

	@JsonProperty("FollowupId")
	private String followupId;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("LoginId")
	private String loginId;

    
    }
