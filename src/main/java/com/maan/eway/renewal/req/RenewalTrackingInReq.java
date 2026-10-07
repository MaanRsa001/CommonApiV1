package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackingInReq {

	@JsonProperty("ApproverLoginId")
	private String approverLoginId;
}
