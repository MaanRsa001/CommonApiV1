package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SavePaymentDepositReq {
	
	@JsonProperty("CbcNo")
	private String cbcNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PaymentType")
	private String paymentType;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("PremiumAmount")
	private String premiumAmount;
	
	@JsonProperty("ChequeNo")
	private String chequeNo;
	
	@JsonProperty("ChequeDate")
	private String chequeDate;
	
	@JsonProperty("AccountNo")
	private String accountNo;
	
	@JsonProperty("IbanNumber")
	private String ibanNumber;
	
	@JsonProperty("MicrNo")
	private String micrNo;
	
	@JsonProperty("PayeeName")
	private String payeeName;
	
	@JsonProperty("ReferenceNo")
	private String referenceNo;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("DepositAmount")
	private String depositAmount;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("BalanceAmount")
	private String balanceAmount;
	
	@JsonProperty("ReceiptNo")
	private String receiptNo;
	
	@JsonProperty("PolicyInsuranceFee")
	private String policyInsuranceFee;
	
	@JsonProperty("VatAmount")
	private String vatAmount;
	
	@JsonProperty("ChargableType")
	private String chargableType;
	
	@JsonProperty("DepositType")
	private String depositType;

	@JsonProperty("DepositNo")
	private String depositNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("RefundDate")
	private Date refundDate;


}
