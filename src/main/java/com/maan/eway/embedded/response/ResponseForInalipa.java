package com.maan.eway.embedded.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseForInalipa {
	
	@JsonProperty("PolicyNumber")
	private String policyNo;
	
	@JsonProperty("ExpiredDate")
	private String expiredDate;
	
	@JsonProperty("TransactionNo")
	private String transactionNo;
	
	@JsonProperty("Schedule")
	private String pdfurl;
	
	@JsonProperty("isError")
	private Boolean isError;
	
	@JsonProperty("Errors")
	private List<String> errors;
	
	@JsonProperty("Premium")
	private BigDecimal premium;
	
	@JsonProperty("Tax")
	private BigDecimal tax;
	
	@JsonProperty("TaxPercent")
	private String taxPercent;
	
	@JsonProperty("TotalPremium")
	private BigDecimal totalPremium;
	
}
