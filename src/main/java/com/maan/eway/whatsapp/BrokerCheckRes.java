package com.maan.eway.whatsapp;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCheckRes {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("WhatsappNo")
	private String whatsappNo;
	
	@JsonProperty("WhatsappCode")
	private String whatsappCode;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private String entryDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private String updatedDate;
	
	@JsonProperty("BranchCode")
	private String branchCode;
}
