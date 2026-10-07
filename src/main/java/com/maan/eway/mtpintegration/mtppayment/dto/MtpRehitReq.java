package com.maan.eway.mtpintegration.mtppayment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MtpRehitReq {
	@JsonProperty("QuoteNo")
    private String quoteNo;
	@JsonProperty("MobileNo")
    private String mobileNo;   
	@JsonProperty("EmployeeName")
    private String employeeName;  
}

