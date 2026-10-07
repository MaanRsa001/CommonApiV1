package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginUserInfoGetRes {
    @JsonProperty("LoginId")
    private String loginId;

    @JsonProperty("OaCode")
    private String oaCode;

    @JsonProperty("AgencyCode")
    private String agencyCode;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("UserMobile")
    private String userMobile;

    @JsonProperty("UserMail")
    private String userMail;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("EntryDate")
    private Date entryDate;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("UpdatedDate")
    private Date updatedDate;

    @JsonProperty("UpdatedBy")
    private String updatedBy;

    @JsonProperty("CompanyName")
    private String companyName;

    @JsonProperty("Address1")
    private String address1;

    @JsonProperty("Address2")
    private String address2;

    @JsonProperty("Address3")
    private String address3;

    @JsonProperty("CityCode")
    private Integer cityCode;

    @JsonProperty("StateCode")
    private String stateCode;

    @JsonProperty("CountryCode")
    private String countryCode;

    @JsonProperty("CityName")
    private String cityName;

    @JsonProperty("Pobox")
    private String pobox;

    @JsonProperty("StateName")
    private String stateName;

    @JsonProperty("Fax")
    private String fax;

    @JsonProperty("CountryName")
    private String countryName;

    @JsonProperty("Remarks")
    private String remarks;

    @JsonProperty("CustomerId")
    private BigDecimal customerId;

    @JsonProperty("BranchCode")
    private String branchCode;

    @JsonProperty("MissippiId")
    private BigDecimal missippiId;

    @JsonProperty("ApprovedPreparedBy")
    private String approvedPreparedBy;

    @JsonProperty("AcExecutiveId")
    private BigDecimal acExecutiveId;

    @JsonProperty("CoreAppBrokerCode")
    private String coreAppBrokerCode;

    @JsonProperty("VatRegNo")
    private String vatRegNo;

    @JsonProperty("CheckerYn")
    private String checkerYn;

    @JsonProperty("MakerYn")
    private String makerYn;

    @JsonProperty("CommissionVatYn")
    private String commissionVatYn;

    @JsonProperty("CustConfirmYn")
    private String custConfirmYn;

    @JsonProperty("ContactPersonName")
    private String contactPersonName;

    @JsonProperty("Designation")
    private String designation;

    @JsonProperty("EffectiveDateStart")
    private Date effectiveDateStart;

    @JsonProperty("MobileCode")
    private String mobileCode;

    @JsonProperty("MobileCodeDesc")
    private String mobileCodeDesc;

    @JsonProperty("WhatsappCode")
    private String whatsappCode;

    @JsonProperty("WhatsappCodeDesc")
    private String whatsappCodeDesc;

    @JsonProperty("WhatsappNo")
    private String whatsappNo;

    @JsonProperty("TaxExemptedYn")
    private String taxExemptedYn;

    @JsonProperty("TaxExemptedCode")
    private String taxExemptedCode;

    @JsonProperty("CreditLimit")
    private BigDecimal creditLimit;

    @JsonProperty("CustomerCode")
    private String customerCode;

    @JsonProperty("CustomerName")
    private String customerName;

    @JsonProperty("RegulatoryCode")
    private String regulatoryCode;

    @JsonProperty("BrokerLogo")
    private String brokerLogo;

    @JsonProperty("IdTypeDesc")
    private String idTypeDesc;

    @JsonProperty("IdType")
    private String idType;

    @JsonProperty("IdNumber")
    private String idNumber;

}
