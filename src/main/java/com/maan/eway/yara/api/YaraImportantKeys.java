package com.maan.eway.yara.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YaraImportantKeys {

//	@JsonProperty("QuoteNo")
//	private String quoteNo;
	
//	@JsonProperty("RequestReferenceNo")
//	private String requestRefNo;
	
//	@JsonProperty("CustomerId")
//	private String customerId;
	
//	@JsonProperty("CustomerReqRefNo")
//	private String customerReqRefNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;
	
//	@JsonProperty("GenderDesc")
//	private String genderDesc;
	
	@JsonProperty("OccupationId")
	private String occupationId;
	
	@JsonProperty("OccupationDesc")
	private String occupationDesc;
	
	@JsonProperty("IdTypeDesc")
	private String idTypeDesc;
	
	@JsonProperty("CityCode")
	private String cityCode;
	
	@JsonProperty("CityName")
	private String cityName;
	
	@JsonProperty("CountryCode")
	private String countryCode;
	
	@JsonProperty("CountryName")
	private String countryName;
	
	@JsonProperty("RegionCode")
	private String regionCode;
	
	@JsonProperty("RegionName")
	private String regionName;
	
	@JsonProperty("MobileCode")
	private String mobileCode;
	
	@JsonProperty("StateCode")
	private String stateCode;
	
	@JsonProperty("CurrencyCode")
	private String currencyCode;
	
	@JsonProperty("ExchangeRate")
	private String exchangeRate;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("CoverName")
	private String coverName;
	
	@JsonProperty("PolicyStartDate")
	private String policyStartDate;
	
	@JsonProperty("PolicyEndDate")
	private String policyEndDate;
	
	
}
