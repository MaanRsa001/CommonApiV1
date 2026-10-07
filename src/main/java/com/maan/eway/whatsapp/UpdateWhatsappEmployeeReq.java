package com.maan.eway.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateWhatsappEmployeeReq {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("SNo")
	private Long sNo;
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("WhatsappCode")
	private String whatsappCode;
	
	@JsonProperty("WhatsappNo")
	private String whatsappNo;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("Status")
	private String status;
	
}
