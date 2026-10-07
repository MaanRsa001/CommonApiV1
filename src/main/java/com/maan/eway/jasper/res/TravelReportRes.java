package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TravelReportRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("TelephoneNo1")
	private String telephoneNo1;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("AgencyName")
	private String agencyName;
	
	@JsonProperty("PassportNo")
	private String passportNo;
	
	@JsonProperty("PolicyPeriod")
	private String policyPeriod;
	
	@JsonProperty("OrginCountry")
	private String orginCountry;
	
	@JsonProperty("DestCountry")
	private String destCountry;
	
	@JsonProperty("PlanDesc")
	private String planDesc;
	
	@JsonProperty("InceptionDate")
	private String inceptionDate;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("OverAllPremium")
	private String overAllPremium;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("NoOfPassanger")
	private String noOfPassanger;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("Companylogo")
	private String companylogo;
	
	@JsonProperty("PassangerDetails")
	private List<TravelDataSetOneRes> passangerDetails;
	
	@JsonProperty("TravelCoverDetails")
	private List<TravelDataSetTwoRes> travelCoverDetails;
	
	@JsonProperty("TravelCoverageDetails")
	private List<TravelCoverageDetailsRes> TravelCoverageDetails;

}
