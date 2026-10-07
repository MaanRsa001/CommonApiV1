package com.maan.eway.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerDetailsSearchRes {

	
	@JsonProperty("CustomerId")
    private String     customerId   ;
	
	@JsonProperty("TitleDesc")
    private String     titleDesc    ;

	@JsonProperty("ClientName")
    private String     clientName   ;
	
	@JsonProperty("InsuranceId")
    private String     companyId    ;

	@JsonProperty("CompanyName")
    private String     companyName    ;
	
	@JsonProperty("BranchCode")
    private String     branchCode   ;
	
	@JsonProperty("BranchName")
    private String    branchName     ;
	
	@JsonProperty("CreatedBy")
    private String     createdBy       ;

	@JsonProperty("UserType")
    private String     userType       ;
	
	@JsonProperty("SubUserType")
    private String     subUserType       ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date       entryDate  ;
	
	@JsonProperty("ProductCount")
    private String     productCount;
}
