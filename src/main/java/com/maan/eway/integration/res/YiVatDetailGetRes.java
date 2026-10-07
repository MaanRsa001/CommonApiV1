package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YiVatDetailGetRes {
	private static final long serialVersionUID = 1L;
	 
	@JsonProperty("RequestReferenceNo")
	private String     requestreferenceno ;

	@JsonProperty("serviceId")
    private String     serviceId ;

	@JsonProperty("quotationPolicyNo")
    private String     quotationPolicyNo ;

	@JsonProperty("serviceAction")
    private String     serviceAction ;

	@JsonProperty("indexNo")
    private String     indexNo ;

	@JsonProperty("vatSrNo")
    private String     vatSrNo ;

	@JsonProperty("vatId")
    private String vatId ;

	@JsonProperty("vatApplyOn")
    private String     vatApplyOn ;

	@JsonProperty("vatCode")
    private String     vatCode ;

	@JsonProperty("vatPerc")
    private String     vatPerc ;

	@JsonProperty("vatAmount")
    private String     vatAmount ;

	@JsonProperty("vatModifiedYn")
    private String     vatModifiedYn ;

	@JsonProperty("prodCode")
    private String     prodCode ;

	@JsonProperty("pWsResponseType")
    private String     pWsResponseType ;

	@JsonProperty("pWsError")
    private String     pWsError ;

	@JsonProperty("requestTime")
	@JsonFormat(pattern = "dd/MM/YYYY")
    private Date       requestTime ;

	@JsonProperty("responseTime")
	@JsonFormat(pattern = "dd/MM/YYYY")
    private Date       responseTime ;

	@JsonProperty("status")
    private String     status ;

}
