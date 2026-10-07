package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetBrokerListDropDownRes {

	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("Status")
	private String  status;
	
	@JsonProperty("OaCode")
	private Integer oaCode ;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("CustomerName")
	private String customerName;
}
