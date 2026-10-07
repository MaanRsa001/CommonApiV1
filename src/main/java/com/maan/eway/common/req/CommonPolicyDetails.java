package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CommonPolicyDetails {
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("ExchangeRate")
	private String exchangeRate;
	
	@JsonProperty("Havepromocode")
	private String havepromocode;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
}
