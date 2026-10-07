package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerDropDownRes {


	@JsonProperty("BrokerId")
	private String brokerId;
	@JsonProperty("BrokerName")
	private String brokerName;
	@JsonProperty("InsuranceId")
	private String companyId;
	@JsonProperty("SubUserType")
	private String subUserType;
	@JsonProperty("CustomerCode")
	private String customerCode;
	@JsonProperty("CustomerName")
	private String customerName;
}
