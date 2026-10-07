package com.maan.eway.integration.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InsertYiPremCalReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("CoverCode")
	private String coverCode;

	@JsonProperty("Requestreferenceno")
	private String requestreferenceno;

	// --- ENTITY DATA FIELDS
	@JsonProperty("ServiceId")
	private String serviceId;

	@JsonProperty("QuotationPolicyNo")
	private String quotationPolicyNo;

	@JsonProperty("IterationNo")
	private String iterationNo;

	@JsonProperty("RiskId")
	private String riskId;

	@JsonProperty("SecCode")
	private String secCode;

	@JsonProperty("CoverDescription")
	private String coverDescription;

	@JsonProperty("CoverPremium")
	private String coverPremium;

	@JsonProperty("SumInsured")
	private String sumInsured;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("RequestTime")
	private Date requestTime;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ResponseTime")
	private Date responseTime;

	@JsonProperty("PWsResponseType")
	private String pWsResponseType;

	@JsonProperty("PWsError")
	private String pWsError;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("RenewalPolicyNo")
	private String renewalPolicyNo;

	@JsonProperty("RenewalCurrentStatus")
	private String renewalCurrentStatus;

	@JsonProperty("ProductCode")
	private String productCode;
	
	@JsonProperty("PremEndNoIdx")
    private String     premEndNoIdx ;
	@JsonProperty("SectionId")
	private String     sectionId ;
	
	 @JsonProperty(  "RECORD_TYPE_ID")
		private String 	recordTypeId;

	// --- ENTITY LINKS ( RELATIONSHIP )

}
