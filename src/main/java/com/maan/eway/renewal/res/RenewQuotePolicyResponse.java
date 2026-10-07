package com.maan.eway.renewal.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewQuotePolicyResponse {

    @JsonProperty("TranId")
    private String tranId;

    @JsonProperty("OldRequestRefNo")
    private String oldRequestRefNo;

    @JsonProperty("ServiceType")
    private String serviceType;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonProperty("RequestTime")
    private Date requestTime;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonProperty("ResponseTime")
    private Date responseTime;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("OldPolicyNo")
    private String oldPolicyNo;

    @JsonProperty("OldQuoteNo")
    private String oldQuoteNo;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("OldStartDate")
    private Date oldStartDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("OldEndDate")
    private Date oldEndDate;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("CustomerName")
    private String customerName;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("Occupation")
    private String occupation;

    @JsonProperty("MobileCode")
    private String mobileCode;

    @JsonProperty("MobileNo")
    private String mobileNo;

    @JsonProperty("EmailId")
    private String emailId;

    @JsonProperty("IdentityType")
    private String identityType;

    @JsonProperty("IdentityNumber")
    private String identityNumber;

    @JsonProperty("PreferredNotification")
    private String preferredNotification;

    @JsonProperty("TaxExcemption")
    private String taxExcemption;

    @JsonProperty("Street")
    private String street;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("District")
    private String district;

    @JsonProperty("Pobox")
    private String pobox;

    @JsonProperty("NoOfVehicle")
    private String noOfVehicle;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("NewStartDate")
    private Date newStartDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("NewEndDate")
    private Date newEndDate;

    @JsonProperty("NewPremium")
    private String newPremium;

    @JsonProperty("NewPolicyNumber")
    private String newPolicyNumber;

    @JsonProperty("CurrentStatus")
    private String currentStatus;

    @JsonProperty("CurrentStageCode")
    private String currentStageCode;

    @JsonProperty("CurrentStatusCode")
    private String currentStatusCode;

    @JsonProperty("LoginId")
    private String loginId;

    @JsonProperty("ApplicationId")
    private String applicationId;

    @JsonProperty("CompanyId")
    private String companyId;

    @JsonProperty("ProductCode")
    private String productCode;

    @JsonProperty("SectionCode")
    private String sectionCode;

    @JsonProperty("BranchCode")
    private String branchCode;

    @JsonProperty("BranchName")
    private String branchName;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("ViewedDate")
    private Date viewedDate;

    @JsonProperty("Remarks")
    private String remarks;
    
    @JsonProperty("RegistrationNo")
	private String registrationNo;
}
