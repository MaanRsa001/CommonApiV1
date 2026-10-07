package com.maan.eway.integration.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InsertYiSectionDetailReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Requestreferenceno")
	private String requestreferenceno;

	@JsonProperty("ServiceId")
	private String serviceId;

	@JsonProperty("ServiceAction")
	private String serviceAction;

	@JsonProperty("QuotationPolicyNo")
	private String quotationPolicyNo;

	@JsonProperty("SrNo")
	private Double srNo;

	@JsonProperty("SectionCode")
	private String sectionCode;

	@JsonProperty("IterationNo")
	private String iterationNo;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("RequestTime")
	private Date requestTime;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ResponseTime")
	private Date responseTime;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("PWsResponseType")
	private String pWsResponseType;

	@JsonProperty("PWsError")
	private String pWsError;

	@JsonProperty("ProductCode")
	private String productCode;

	@JsonProperty("SchemeDesc")
	private String schemeDesc;
	
	@JsonProperty("RiskId")
    private String riskId;
	
	@JsonProperty("SectionId")
	private String     sectionId ;
	
	 @JsonProperty(  "RECORD_TYPE_ID")
		private String 	recordTypeId;
}
