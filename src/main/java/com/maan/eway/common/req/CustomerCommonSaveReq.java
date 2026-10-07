package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerCommonSaveReq {
	
	@JsonProperty("CustomerId")
    private String     customerId   ;
	@JsonProperty("CreatedBy")
    private String     createdBy;
	
	@JsonProperty("GstNo")
    private String     gstNo        ;	
	@JsonProperty("NameTitleId")
    private String     nameTitleId   ;
	
	@JsonProperty("PolicyHolderName")
    private String    policyHolderName     ;	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyHolderBirthDate")
    private Date   policyHolderBirthDate     ;	
	@JsonProperty("PolicyHolderTypeId")
    private String    policyHolderTypeId     ;
	@JsonProperty("PolicyHolderIdNumber")
    private String    policyHolderIdNumber     ;	
	@JsonProperty("PolicyHolderIdTypeId")
    private String    policyHolderIdTypeId     ;
	@JsonProperty("PolicyHolderMobileNumber")
    private String    policyHolderMobileNumber     ;
	@JsonProperty("PolicyHolderFax")
    private String    policyHolderFax     ;
	@JsonProperty("PostalAddress")
    private String    postalAddress     ;
	@JsonProperty("EmailAddress")
    private String    emailAddress     ;
	
	@JsonProperty("GenderId")
    private String     genderId  ;
	@JsonProperty("CountryCode")
    private String     countryCode  ;
	@JsonProperty("RegionCode")
    private String     regionCode   ;
	@JsonProperty("BranchCode")
    private String     branchCode   ;
	@JsonProperty("StateCode")
    private String    stateCode    ;
	@JsonProperty("CityCode")
    private String    cityCode     ;
	
	

}
