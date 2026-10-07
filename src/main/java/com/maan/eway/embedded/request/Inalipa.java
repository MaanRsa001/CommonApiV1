package com.maan.eway.embedded.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Inalipa {
	
	/*
	 * NIDAno
Mobileno
CustomerName
ServiceOpted
AmountPaid
Transactionno
Entrydatetime
	 */
	@JsonProperty("InsuredNIDANo") 
	private String NIDA_Number;
	
	@JsonProperty("MobileCode")
	private String mobileCode;
	
	@JsonProperty("MobileNumber")
	private String mobileNumber;
		
	@JsonProperty("InsuredName")
	private String insurerName;
	
	@JsonProperty("PlanOpted")
	private String planOpted;
	
	@JsonProperty("OrderValue")
	private BigDecimal orderValue;
	
	@JsonProperty("TransactionNo")
	private String transactionNo;
	
	@JsonProperty("OrderDate")
	private String orderDate;
	
}
