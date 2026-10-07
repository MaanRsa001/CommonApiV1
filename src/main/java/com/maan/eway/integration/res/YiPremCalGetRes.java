package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YiPremCalGetRes {
	
	private static final long serialVersionUID = 1L;
	 
	@JsonProperty("CoverCode")
    private String     coverCode ;

	@JsonProperty("RequestReferenceNo")
    private String     requestreferenceno ;

	@JsonProperty("ServiceId")
    private String     serviceId ;

	@JsonProperty("QuotationPolicyNo")
    private String     quotationPolicyNo ;

	@JsonProperty("IterationNo")
    private String     iterationNo ;

	@JsonProperty("RiskId")
    private String     riskId ;

	@JsonProperty("SecCode")
    private String     secCode ;

	@JsonProperty("CoverDescription")
    private String     coverDescription ;

	@JsonProperty("CoverPremium")
    private String    coverPremium ;

	@JsonProperty("SumInsured")
    private String   sumInsured ;

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

	@JsonProperty("status")
    private String     status ;

	@JsonProperty("RenewalPolicyNo")
    private String     renewalPolicyNo ;

	@JsonProperty("RenewalCurrentStatus")
    private String     renewalCurrentStatus ;

	@JsonProperty("ProductCode")
    private String     productCode ;

}
