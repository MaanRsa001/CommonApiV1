package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotDriverDetailGetRes {
	
	private static final long serialVersionUID = 1L;
	 
	@JsonProperty("RequestReferenceNo")
    private String     requestreferenceno ;

	@JsonProperty("SerialNo")
    private String     serialNo ;

	@JsonProperty("ServiceId")
    private String     serviceId ;

	@JsonProperty("QuotationPolicyNo")
    private String     quotationPolicyNo ;

	@JsonProperty("SecCode")
    private String     secCode ;

	@JsonProperty("L1s2Id")
    private String       l1s2Id ;

	@JsonProperty("IterationNo")
    private String     iterationNo ;

	@JsonProperty("Name")
    private String     name ;

	@JsonProperty("DateOfBirth")
    private Date       dateOfBirth ;

	@JsonProperty("Sex")
    private String     sex ;

	@JsonProperty("IdResidentNo")
    private String     idResidentNo ;

	@JsonProperty("TypeOfClass")
    private String     typeOfClass ;

	@JsonProperty("RequestTime")
    private Date       requestTime ;

	@JsonProperty("ResponseTime")
    private Date       responseTime ;

	@JsonProperty("Status")
    private String     status ;

	@JsonProperty("PWsResponseType")
    private String     pWsResponseType ;

	@JsonProperty("PWsError")
    private String     pWsError ;

	@JsonProperty("EntryDate")
    private Date       entryDate ;

	@JsonProperty("QuoteNo")
    private String    quoteNo ;

	@JsonProperty("RenewalPolicyNo")
    private String     renewalPolicyNo ;

	@JsonProperty("ProductCode")
    private String     productCode ;

	@JsonProperty("LicenseIssuedOn")
    private Date       licenseIssuedOn ;

}
