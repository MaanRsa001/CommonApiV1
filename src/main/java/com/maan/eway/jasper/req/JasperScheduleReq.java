package com.maan.eway.jasper.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JasperScheduleReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("TaxShowYn")
	private String taxShowYn;
	
	@JsonProperty("ReportId")
	private String reportId;
	
	@JsonProperty("PremiumReportReq")
	private PremiumReportReq premiumRegisterReq;
}
