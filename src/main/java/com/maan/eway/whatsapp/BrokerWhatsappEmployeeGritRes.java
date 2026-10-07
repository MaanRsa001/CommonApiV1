package com.maan.eway.whatsapp;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerWhatsappEmployeeGritRes {

	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("SNo")
	private Long sNo;
	
	@JsonProperty("WhatsappCode")
	private String whatsappCode;
	
	@JsonProperty("WhatsappNo")
	private String whatsappNo;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private String updatedDate;
}
