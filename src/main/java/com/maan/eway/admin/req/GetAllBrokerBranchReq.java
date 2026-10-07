package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetAllBrokerBranchReq {

	@JsonProperty("LoginId")
	private String loginId ;
}
