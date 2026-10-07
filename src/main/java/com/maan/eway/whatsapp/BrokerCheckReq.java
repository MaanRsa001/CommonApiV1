package com.maan.eway.whatsapp;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCheckReq {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("Action")
	private String action;
	
	@JsonProperty("WhatsappNo")
	private String whatsappNo;
	
	@JsonProperty("EmployeeDetails")
	private List<WhatsappEmployeeReq> employeeDetails;
	
	@JsonProperty("SNo")
	private Long sNo;
}
