package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DasboardReferalPendingRes {

	@JsonProperty("RequestReferencNo")
    private String     requestReferencNo     ;
	@JsonProperty("CustomerName")
    private String     customerName     ;
	@JsonProperty("PolicyStartDate")
	private String policyStartDate;
	@JsonProperty("PolicyEndtDate")
	private String policyEndDate;
	@JsonProperty("Status")
    private String     status     ;
		
}
