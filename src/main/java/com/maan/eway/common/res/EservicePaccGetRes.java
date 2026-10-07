package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EservicePaccGetRes {

	@JsonProperty("RequestReferenceNo")
    private String     requestReferenceNo ;
	@JsonProperty("RiskId")
    private String    riskId   ;
	@JsonProperty("CustomerReferenceNo")
    private String     customerReferenceNo ;
	@JsonProperty("ProductId")
    private String    productId    ;
	@JsonProperty("InsuranceId")
    private String     companyId    ;
	@JsonProperty("BranchCode")
    private String     branchCode   ;
	
	@JsonProperty("SectionId")
    private String sectionId    ;
	
	@JsonProperty("SectionDesc")
    private String sectionDesc   ;
	@JsonProperty("Suminsured")
    private String     suminsured ;
	
	@JsonProperty("PolicyPeriod")
    private String     policyPeriod   ;
	
	@JsonProperty("CreatedBy")
    private String     createdBy    ;
	
	@JsonProperty("AcExecutiveId")
    private String    acExecutiveId ;
	@JsonProperty("ApplicationId")
    private String     applicationId ;
	@JsonProperty("BrokerCode")
    private String     brokerCode   ;
	@JsonProperty("SubUsertype")
    private String     subUserType  ;
	@JsonProperty("LoginId")
    private String     loginId      ;
	@JsonProperty("AgencyCode")
    private String     agencyCode   ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
    private Date       policyStartDate ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
    private Date       policyEndDate ;
	
	@JsonProperty("Currency")
    private String     currency     ;
	@JsonProperty("ExchangeRate")
    private String     exchangeRate ;
	
	
	@JsonProperty("BrokerBranchCode")
    private String     brokerBranchCode  ;
	
	@JsonProperty("Havepromocode")
    private String     havepromocode;
	
	@JsonProperty("Promocode")
    private String     promocode;
	
		
	@JsonProperty("OccupationType")
    private String    occupationType;

	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("CategoryId")
    private String    categoryId;

	@JsonProperty("CustomerName")
    private String    customerName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("Dob")
    private Date    dob;
}
