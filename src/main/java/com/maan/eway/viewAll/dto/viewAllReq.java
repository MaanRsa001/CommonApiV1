package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class viewAllReq {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("BrokerQouotation")
	private String brokerQouotation;
	
}
