package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YiPolicyApprovalGetRes {
	private static final long serialVersionUID = 1L;
	 
	@JsonProperty("RequestReferenceNo")
    private String     requestreferenceno ;
	
	@JsonProperty("ServiceId")
    private String     serviceId ;

	@JsonProperty("QuotationPolicyNo")
    private String     quotationPolicyNo ;

	@JsonProperty("IterationNo")
    private String     iterationNo ;

	@JsonProperty("ApprDesc")
    private String     apprDesc ;

	@JsonProperty("RequestTime")
	@JsonFormat(pattern = "dd/MM/yyyy")
    private Date       requestTime ;

	@JsonProperty("ResponseTime")
	@JsonFormat(pattern = "dd/MM/yyyy")
    private Date       responseTime ;

	@JsonProperty("PWsResponseType")
    private String     pWsResponseType ;

	@JsonProperty("PWsError")
    private String     pWsError ;

	@JsonProperty("Status")
    private String     status ;

	@JsonProperty("RenewalPolicyNo")
    private String     renewalPolicyNo ;

	@JsonProperty("RenewalCurrentStatus")
    private String     renewalCurrentStatus ;

	@JsonProperty("TranCode")
    private String     tranCode ;

	@JsonProperty("DocNo")
    private String     docNo ;


}
