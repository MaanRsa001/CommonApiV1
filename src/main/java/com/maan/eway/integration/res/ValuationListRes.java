package com.maan.eway.integration.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationListRes {
	
	@JsonProperty("ValCompanyId")
	private String valCompanyId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("FirstName")
	private String firstName;
	
	@JsonProperty("VehicleId")
	private String vehicleId;
	
	@JsonProperty("VehicleRegNo")
	private String vehicleRegNo;
	
	@JsonProperty("CustomerMobile")
	private String customerMobile;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("RecordId")
	private String recordId;
	
	@JsonProperty("MasterrecordId")
	private String masterrecordId;
	
	
	@JsonProperty("CreateRequest")
	private String createRequest;
	
	@JsonProperty("CreateResponse")
	private String createResponse;
	
	@JsonProperty("Idrequest")
	private String idrequest;
	
	@JsonProperty("Idresponse")
	private String idresponse;
	
	@JsonProperty("Statusrequest")
	private String statusrequest;
	
	@JsonProperty("Statusresponse")
	private String statusresponse;
	
	@JsonProperty("ExceptionStatus")
	private String exceptionStatus;
	
	@JsonProperty("ExceptionSumInusred")
	private String exceptionSumInusred;
	
	@JsonProperty("ExceptionRemarks")
	private String exceptionRemarks;
	
	
}
