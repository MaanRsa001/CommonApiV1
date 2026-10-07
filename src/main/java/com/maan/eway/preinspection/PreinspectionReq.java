package com.maan.eway.preinspection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PreinspectionReq {

    @JsonProperty("ChassisNo")
    private String chassisNo;

    @JsonProperty("RegistrationNo")
    private String registrationNo;

    @JsonProperty("QuoteNo")
    private String quoteNo;

    @JsonProperty("CustomerName")
    private String customerName;

    @JsonProperty("MobileNo")
    private String mobileNo;

    @JsonProperty("PolicyStartDate")
    private String policyStartDate;

    @JsonProperty("PolicyEndDate")
    private String policyEndDate;

    @JsonProperty("ProductId")
    private String productId;

    @JsonProperty("ProductName")
    private String productName;

    @JsonProperty("SectionId")
    private String sectionId;

    @JsonProperty("SectionName")
    private String sectionName;

    @JsonProperty("CompanyId")
    private String companyId;

    @JsonProperty("EntryDate")
    private String entryDate;

    @JsonProperty("Premium")
    private String premium;
}


