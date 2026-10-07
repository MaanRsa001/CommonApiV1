package com.maan.eway.yara.api;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YaraDocumentResp {

	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("FirstName")
	private String firstName;
	
	@JsonProperty("MiddleName")
	private String middleName;
	
	@JsonProperty("LastName")
	private String lastName;
	
	@JsonProperty("CompanyName")
	private String comapanyName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("LimitsOfLiability")
	private String limitsOfLiability;
	
	@JsonProperty("Rate")
	private String rate;
	
	@JsonProperty("PremiumAmount")
	private String premiumAmount;
	
	@JsonProperty("PremiumCurrency")
	private String premiumCurrency;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date       effectiveDate ;
	
	@JsonProperty("TitleDesc")
	private String     titleDesc ;
	
	@JsonProperty("Region")
	private String     region ;
	
	@JsonProperty("District")
	private String     district ;
	
	@JsonProperty("NoofAcres")
	private Integer     noOfAcres ;
	
	@JsonProperty("LocationName")
	private String     locationName ;
	
	@JsonProperty("LocationAddress")
	private String     locationAddress ;
	
	@JsonProperty("DistributorId")
	private String     distributorId ;
	
	@JsonProperty("DistributorDesc")
	private String     distributorDesc ;
	
	@JsonProperty("CoveragePercentageId")
	private Integer     coverage ;
}
