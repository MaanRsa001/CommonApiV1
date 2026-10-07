package com.maan.eway.integration.req;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YiCoverDetailReq {

	@JsonProperty("QuotationPolicyNo")
	private String quotationPolicyNo;

	@JsonProperty("ServiceId")
	private String serviceId;

	@JsonProperty("ServiceAction")
	private String serviceAction;

	@JsonProperty("SecCode")
	private String secCode;

	@JsonProperty("L1s1Id")
	private Double l1s1Id;

	@JsonProperty("CvrId")
	private Double cvrId;

	@JsonProperty("CoverCode")
	private String coverCode;

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
	private Date responseTime;


	@JsonProperty("RequestTime")
	private Date requestTime;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("PWsResponseType")
	private String pWsResponseType;

	@JsonProperty("PWsError")
	private String pWsError;

	@JsonProperty("Requestreferenceno")
	private String requestreferenceno;

	@JsonProperty("RenewalPolicyNo")
	private String renewalPolicyNo;

	@JsonProperty("RenewalCurrentStatus")
	private String renewalCurrentStatus;

	@JsonProperty("ProdCode")
	private String prodCode;

	@JsonProperty("ServiceType")
	private String serviceType;

	@JsonProperty("CoverDesc")
	private String coverDesc;
	
	@JsonProperty("RiskId")
    private Integer riskId;
	
	@JsonProperty("PrcCode")
	private String prcCode;

	@JsonProperty("PrcDesc")
	private String prcDesc;

	@JsonProperty("PrcRate")
	private String prcRate;

	@JsonProperty("PrcRatePer")
	private String prcRatePer;

	@JsonProperty("PrcCvrType")
	private String prcCvrType;

	@JsonProperty("PrcSiFc")
	private String prcSiFc;

	@JsonProperty("PrcSiLc1")
	private String prcSiLc1;

	@JsonProperty("PrcPremFc")
	private String prcPremFc;

	@JsonProperty("PrcPremLc1")
	private String prcPremLc1;


	@JsonProperty("CvrEndNoIdx")
	private String     cvrEndNoIdx ;
	
	@JsonProperty("SectionId")
	private String     sectionId ;
	
	 @JsonProperty(  "RECORD_TYPE_ID")
		private String 	recordTypeId;
}
