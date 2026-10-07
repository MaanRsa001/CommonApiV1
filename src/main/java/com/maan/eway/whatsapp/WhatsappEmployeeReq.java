package com.maan.eway.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WhatsappEmployeeReq {

	@JsonProperty("WhatsappNo")
	private String whatsappNo;
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("BrokerLoginId")
	private String brokerLoginId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("WhatsappCode")
	private String whatsappCode;
}
