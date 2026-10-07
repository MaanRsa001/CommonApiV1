package com.maan.eway.whatsapp;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerWhatsAppGritRes {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private String entryDate;
	
	@JsonProperty("EmployeeDetails")
	private List<BrokerWhatsappEmployeeGritRes> employeeDetails;
}
