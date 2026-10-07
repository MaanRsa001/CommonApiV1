package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class PaymentInfoGetRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("PaymentId")
    private String     paymentId ;

	@JsonProperty("CustomerName")
    private String     customerName ;

	@JsonProperty("MobileNo")
    private String     mobileNo ;

	@JsonProperty("EmailId")
    private String     emailId ;

    @JsonProperty("ProductId")
    private String    productId ;
    
    @JsonProperty("ProductDesc")
    private String    productDesc;
    
    @JsonProperty("InsuranceId")
    private String    companyId ;

    @JsonProperty("CompanyName")
    private String    companyName ;

    @JsonFormat(pattern="dd/MM/yyyyy")
    @JsonProperty("PolicyStartDate")
    private Date       policyStartDate ;

    @JsonFormat(pattern="dd/MM/yyyyy")
    @JsonProperty("PolicyEndDate")
    private Date       policyEndDate ;

    @JsonProperty("Premium")
    private String premium ;

    @JsonProperty("Remarks")
    private String     remarks ;

    @JsonProperty("status")
    private String     status ;

    @JsonProperty("paymentStatus")
    private String     paymentStatus ;

    @JsonProperty("AmentId")
    private String     amentId ;

    @JsonProperty("Address1")
    private String     address1 ;

    @JsonProperty("MerchantReference")
    private String     merchantReference ;

    @JsonFormat(pattern="dd/MM/yyyyy")
    @JsonProperty("ValidityDate")
    private Date       validityDate ;

    @JsonProperty("ShorternUrl")
    private String     shorternUrl ;
    
    @JsonProperty("LoginId")
    private String     loginId ;

    @JsonProperty("CustomerCity")
    private String     customerCity ;

    @JsonProperty("EmiYn")
    private String     emiYn ;
    
    @JsonProperty("BranchCode")
    private String     branchCode ;

    @JsonProperty("BranchName")
    private String     branchName ;
    
    @JsonProperty("UserType")
    private String     userType;

    @JsonProperty("SubUserType")
    private String     subUserType ;

    @JsonFormat(pattern="dd/MM/yyyyy")
    @JsonProperty("EntryDate")
    private Date       entryDate ;

    @JsonProperty("CreatedBy")
    private String     createdBy;

    @JsonFormat(pattern="dd/MM/yyyyy")
    @JsonProperty("UpdatedDate")
    private Date       updatedDate ;

    @JsonProperty("UpdatedBy")
    private String     updatedBy;
    
	@JsonProperty("InstallmentMonth")
	private String installmentMonth;
	
	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;
}
