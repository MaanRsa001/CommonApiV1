package com.maan.eway.jasper.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PdfJsonReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("TaxInvoiceYn")
	private String taxInvoiceYn;
	
	@JsonProperty("CreditYn")
	private String creditYn;
	
	@JsonProperty("BrokerQuotationYn")
	private String brokerQuotationYn;
	
	@JsonProperty("EndtSchedule")
	private String endtSchedule;
	
	@JsonProperty("VehicleId")
	private String vehicleId;
	
	
}
