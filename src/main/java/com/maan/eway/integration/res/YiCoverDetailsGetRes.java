package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YiCoverDetailsGetRes {
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("ChargeCode")
	private String quotationPolicyNo ;

	@JsonProperty("ServiceId")
    private String serviceId ;

	@JsonProperty("ServiceAction")
    private String serviceAction ;

	@JsonProperty("SecCode")
    private String secCode ;

	@JsonProperty("11s1Id")
    private Double l1s1Id;

	@JsonProperty("CvrId")
    private Double cvrId;

	@JsonProperty("CoverCode")
    private String coverCode ;

	@JsonProperty("SumInsured")
    private String sumInsured;
    
	@JsonProperty("IterationNo")
    private String iterationNo;
    
	@JsonProperty("SiModifiedYn")
    private String siModifiedYn;
    
	@JsonProperty("Rate")
    private Double rate;
    
	@JsonProperty("RateModifiedYn")
    private String rateModifiedYn;
    
	@JsonProperty("Premium")
    private Double premium;
    
	@JsonProperty("PremiumModifiedYn")
    private String premiumModifiedYn;
    
	@JsonProperty("ResponseTime")
	@JsonFormat(pattern = "dd/MM/yyyy")
    private Date responseTime ;

	@JsonProperty("RequestTime")
	@JsonFormat(pattern = "dd/MM/yyyy")
    private Date requestTime ;
    
	@JsonProperty("Status")
    private String status ;

	@JsonProperty("PWsResponseType")
    private String pWsResponseType ;

	@JsonProperty("PWsError")
    private String pWsError ;
    
	@JsonProperty("Requestreferenceno")
    private String requestreferenceno ;

	@JsonProperty("RenewalPolicyNo")
    private String renewalPolicyNo ;
    
	@JsonProperty("RenewalCurrentStatus")
    private String renewalCurrentStatus ;

	@JsonProperty("ProdCode")
    private String prodCode;

	@JsonProperty("ServiceType")
    private String serviceType;
    
	@JsonProperty("CoverDesc")
    private String coverDesc;
	
	@JsonProperty("RiskId")
	private String riskId;
}
