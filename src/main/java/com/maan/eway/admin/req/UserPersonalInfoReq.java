package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserPersonalInfoReq {

	// Personal Details
	@JsonProperty("UserName")
    private String     userName     ;
	@JsonProperty("UserMobile")
    private String     userMobile   ;
	@JsonProperty("UserMail")
    private String     userMail     ;

	@JsonProperty("CompanyName")
    private String    companyName ;
    
    @JsonProperty("Address1")
    private String    address1 ;
    
    @JsonProperty("Address2")
    private String    address2 ;
    
    @JsonProperty("Address3")
    private String    address3 ;
    
    @JsonProperty("CityCode")
    private String    cityCode ;
    
    @JsonProperty("StateCode")
    private String    stateCode ;
    
    @JsonProperty("CountryCode")
    private String    countryCode ;
    
    @JsonProperty("CoreAppBrokerCode")
    private String    coreAppBrokerCode ;
    
    @JsonProperty("Pobox")
    private String    pobox ;
    
    @JsonProperty("Fax")
    private String    fax ;
    
  
    @JsonProperty("Remarks")
    private String    remarks ;
   
    @JsonProperty("MissippiId")
    private String    missippiId ;
    
    @JsonProperty("ApprovedPreparedBy")
    private String    approvedPreparedBy ;
    
    @JsonProperty("AcExecutiveId")
    private String    acExecutiveId ;
    
//    @JsonProperty("CustConfirmYn")
//    private String    custConfirmYn ;
    
//    @JsonProperty("CommissionVatYn")
//    private String    commissionVatYn ;
//    
//    @JsonProperty("VatRegNo")
//    private String    vatRegNo ;
    
//    @JsonProperty("CheckerYn")
//    private String    checkerYn ;
//    
//    @JsonProperty("MakerYn")
//    private String    makerYn ;
    
	@JsonProperty("TaxExemptedYn")
    private String    taxExemptedYn ;
	
	@JsonProperty("CreditLimit")
    private String    creditLimit ;
    
    @JsonProperty("Designation")
    private String    designation;
    
    @JsonProperty("ContactPersonName")
    private String    contactPersonName ;
    
    
    @JsonProperty("MobileCode")
    private String    mobileCode ;
    
    
    @JsonProperty("WhatsappCode")
    private String    whatsappCode ;
    
    @JsonProperty("WhatsappNo")
    private String    whatsappNo ;
    
    @JsonProperty("CityName")
    private String    cityName;

    @JsonProperty("CustomerCode")
    private String     customerCode     ;
    
    @JsonProperty("CustomerName")
    private String     customerName;
    
    @JsonProperty("RegulatoryCode")
    private String    regulatoryCode;
    
    @JsonProperty("IdNumber")
    private String idNumber;
    
    @JsonProperty("IdType")
    private String idType;

}
