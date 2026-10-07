package com.maan.eway.integration.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InsertYiPolicyApprovalReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Requestreferenceno")
	private String requestreferenceno;

	@JsonProperty("ServiceId")
	private String serviceId;

	@JsonProperty("QuotationPolicyNo")
	private String quotationPolicyNo;

	@JsonProperty("IterationNo")
	private String iterationNo;

	@JsonProperty("ApprDesc")
	private String apprDesc;

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

	@JsonProperty("TranCode")
	private String tranCode;

	@JsonProperty("DocNo")
	private String docNo;
	

	@JsonProperty("AprEndNoIdx")
    private String     aprEndNoIdx ;
	
	
	 @JsonProperty(  "RECORD_TYPE_ID")
		private String 	recordTypeId;

}
