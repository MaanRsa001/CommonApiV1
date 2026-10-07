package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetDepositPaymentRes {
	
	@JsonProperty("CbcNo")
	private String cbcNo;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PaymentType")
	private String paymentType;
	
	@JsonProperty("PaymentTypeDesc")
	private String paymentTypeDesc;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("EntryDate")
	private String entryDate;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ChequeNo")
	private String chequeNo;
	
	@JsonProperty("ChequeDate")
	private String chequeDate;
	
	@JsonProperty("AccountNumber")
	private String accountNumber;
	
	@JsonProperty("IbanNumber")
	private String ibanNumber;
	
	@JsonProperty("MicrNo")
	private String micrNo;
	
	@JsonProperty("PayeeName")
	private String payeeName;
	
	@JsonProperty("ReferenceNo")
	private String referenceNo;
	
	@JsonProperty("DepositNo")
	private String depositNo;
	
	@JsonProperty("DepositType")
	private String depositType;
	
}
