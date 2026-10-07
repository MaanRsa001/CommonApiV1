package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerPersonalInfoGetRes {

	// Personal Details
	@JsonProperty("UserName")
    private String     userName     ;
	@JsonProperty("UserMobile")
    private String     userMobile   ;
	@JsonProperty("UserMail")
    private String     userMail     ;
	
	@JsonProperty("Address1")
    private String    address1 ;
    
    @JsonProperty("Address2")
    private String    address2 ;
    
    @JsonProperty("CityName")
    private String    cityName;
    

    @JsonProperty("StateCode")
    private String    stateCode;
    
    
    @JsonProperty("CountryCode")
    private String    countryCode;
    
    @JsonProperty("MobileCode")
    private String    mobileCode ;
    
    @JsonProperty("Remarks")
    private String    remarks ;
    
    @JsonProperty("WhatappCode")
    private String    whatsappCode ;
    
    @JsonProperty("WhatsappNo")
    private String    whatsappNo ;

    @JsonProperty("LoginAccess")
    private String LoginAccess;
    @JsonProperty("RefferalAccess")
    private String ReferralAccess;
    @JsonProperty("ReportAccess")
    private String ReportAccess;

    @JsonProperty("ClaimCustomerAccess")
    private String ClaimCustomerAccess;


}
