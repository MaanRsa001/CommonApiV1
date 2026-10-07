package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CommonPersonalInforReq {

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
    
    @JsonProperty("CityName")
    private String    cityName ;
    
    @JsonProperty("StateCode")
    private String    stateCode ;
    
    @JsonProperty("StateName")
    private String    stateName ;
    
    @JsonProperty("CountryCode")
    private String    countryCode ;
    
    @JsonProperty("CountryName")
    private String    countryName ;
    
    @JsonProperty("Pobox")
    private String    pobox ;
    
    @JsonProperty("Fax")
    private String    fax ;
    
    @JsonProperty("Remarks")
    private String    remarks ;
    
    @JsonProperty("CustomerId")
    private String    customerId ;
    
    @JsonProperty("MissippiId")
    private String    missippiId ;
    
    @JsonProperty("ApprovedPreparedBy")
    private String    approvedPreparedBy ;
    
    @JsonProperty("CoreAppBrokerCode")
    private String    coreAppBrokerCode ;
    
    @JsonProperty("AcExecutiveId")
    private String    acExecutiveId ;
    
    @JsonProperty("CustConfirmYn")
    private String    custConfirmYn ;
    
    @JsonProperty("CommissionVatYn")
    private String    commissionVatYn ;
    
    @JsonProperty("VatRegNo")
    private String    vatRegNo ;
    
    @JsonProperty("CheckerYn")
    private String    checkerYn ;
    
    @JsonProperty("MakerYn")
    private String    makerYn ;
    
    @JsonProperty("Designation")
    private String    designation;
    
    @JsonProperty("TaxExemptedYn")
    private String    taxExemptedYn ;
    
    @JsonProperty("TaxExemptedCode")
    private String    taxExemptedCode ;
	
	@JsonProperty("CreditLimit")
    private String    creditLimit ;
    
    @JsonProperty("ContactPersonName")
    private String    contactPersonName ;
    
    @JsonFormat(pattern ="dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date    effectiveDateStart ;
    
    @JsonProperty("MobileCode")
    private String    mobileCode ;
    
    @JsonProperty("CustomerCode")
    private String    customerCode ;
    
    @JsonProperty("WhatsappCode")
    private String    whatsappCode ;
    
    @JsonProperty("WhatsappNo")
    private String    whatsappNo ;
    
    @JsonProperty("RegulatoryCode")
    private String    regulatoryCode;
    
    @JsonProperty("CustomerName")
    private String    customerName ;
    
    @JsonProperty("IdNumber")
    private String idNumber;
    
    @JsonProperty("IdType")
    private String idType;
    
    @JsonProperty("CRNo")
    private String cRNo;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("CRExpiryDate")
    private Date crexpiryDate;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("VatExpiryDate")
    private Date vatExpiryDate;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("TaxExpiryDate")
    private Date taxExpiryDate;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("ActivityLicenseExpiry")
    private Date activityExpiryDate;
    
    
    @JsonProperty("TaxCardNo")
    private String taxCardNo;
    
    @JsonProperty("ActivityLicenseno")
    private String activityLicenseno;

    @JsonProperty("AreaLocality")
    private String areaLocality;
  

}
