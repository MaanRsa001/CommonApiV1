package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerPersonalInfoReq {

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
    
    @JsonProperty("CityCode")
    private String    cityCode;
    
    @JsonProperty("CityName")
    private String    cityName;
    
    
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
    
    @JsonProperty("StateCode")
    private String    stateCode;
    
    @JsonProperty("StateName")
    private String    stateName;
    
    @JsonProperty("CountryName")
    private String    countryName;
    
}
