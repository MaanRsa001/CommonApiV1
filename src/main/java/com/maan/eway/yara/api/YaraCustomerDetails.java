package com.maan.eway.yara.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YaraCustomerDetails {

	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("DateOfBirth")
	private String dateOfBirth;
	
	@JsonProperty("IdNumber")
	private String idNumber;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("Gender")
	private String gender;
	
//	@JsonProperty("CountryCode")
//	private String countryCode;
	
	@JsonProperty("Region")
	private String region;
	
	@JsonProperty("District")
	private String district;
	
//	@JsonProperty("Street")
//	private String street;
	
	@JsonProperty("PhoneNumber")
	private String phoneNumber;
	
	@JsonProperty("PostalAddress")
	private String postalAddress;
	
	@JsonProperty("EmailAddress")
	private String emailAddress;
	
//	@JsonProperty("Nationality")
//	private String nationality;
	
}
