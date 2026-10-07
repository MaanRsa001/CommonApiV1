package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class SearchPaymentInfoRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("ProductId")
	private Integer productId;

	@JsonProperty("PaymentId")
	private String paymentId;

	@JsonProperty("PaymentTypedesc")
	private String paymentTypedesc;

	@JsonProperty("PaymentStatus")
	private String paymentStatus;

	@JsonProperty("PayeeName")
	private String payeeName;

	@JsonProperty("MerchantReference")
	private String merchantReference;

	//@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@JsonProperty("EntryDate")
	private String entryDate;

	//@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@JsonProperty("UpdatedDate")
	private String updatedDate;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonProperty("PremiumLc")
	private Double premiumLc;

	@JsonProperty("Premium")
	private BigDecimal premium;

	@JsonProperty("ReqBillToSurname")
	private String reqBillToSurname;
	@JsonProperty("ReqBillToAddressCity")
	private String reqBillToAddressCity;
	@JsonProperty("ReqCardExpiryDate")
	private String reqCardExpiryDate;
	@JsonProperty("ReqBillToAddrPostalCode")
	private String reqBillToAddrPostalCode;
	@JsonProperty("ReqBillToPhone")
	private String reqBillToPhone;
	@JsonProperty("ReqBillToForename")
	private String reqBillToForename;
	@JsonProperty("AuthTime")
	private String authTime;
	@JsonProperty("ReqBilltoEmail")
	private String reqBillToEmail;
	@JsonProperty("ReqBillToCompanyname")
	private String reqBillToCompanyName;
	@JsonProperty("ReqTransactionType")
	private String reqTransactionType;
	@JsonProperty("ReqReferenceNumber")
	private String reqReferenceNumber;
	@JsonProperty("ReqBillToAddressState")
	private String reqBillToAddressState;
	@JsonProperty("ReqBillToAddressLine2")
	private String reqBillToAddressLine2;
	@JsonProperty("ReqBillToAddressLine1")
	private String reqBillToAddressLine1;
	@JsonProperty("ExchangeRate")
	private Double exchangeRate;
	@JsonProperty("EmiYn")
	private String emiYn;

	@JsonProperty("InstallmentMonth")
	private String installmentMonth;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;

//
//	@JsonProperty("PaymentReferenceNo")
//    private String     paymentReferenceNo ;

//	@JsonProperty("CustomerEmail")
//    private String     customerEmail ;
//	@JsonProperty("CustomerName")
//    private String     customerName ;
//	@JsonProperty("ReqCardNumber")
//    private String     reqCardNumber ;
//	@JsonProperty("ReqSignature")
//    private String     reqSignature ;

//	@JsonProperty("ReasonCode")
//    private String     reasonCode   ;

//	@JsonProperty("BillTransRefno")
//    private String     billTransRefNo ;

//	@JsonProperty("RequestToken")
//    private String     requestToken ;

//	@JsonProperty("Result")
//	private String Result;
//	@JsonProperty("Transid")
//	private String Transid;
//	@JsonProperty("Amount")
//	private String amount;
//	@JsonProperty("Trackid")
//	private String Trackid;
//	@JsonProperty("Referenceid")
//	private String referenceid;
//	@JsonProperty("Customerid")
//	private String customerid;
//
//	@JsonProperty("OthProductId")
//	private String othProductId;
//	@JsonProperty("PosCardType")
//	private String posCardType;
//	@JsonProperty("PosApprovedId")
//	private String posApprovedId;
//	@JsonProperty("ReqPaymentType")
//	private String reqPaymentType;
//	@JsonProperty("PosRefNumber")
//	private String posRefNumber;
//	@JsonProperty("ReasonCodeDesc")
//	private String reasonCodeDesc;
//	@JsonProperty("CurrentStageCode")
//	private String currentStageCode;
//	@JsonProperty("CurrentStatusCode")
//	private String currentStatusCode;
//
////
////	@JsonProperty("DateOfCollection")
////    private Date       dateOfCollection ;
//	@JsonProperty("TransactionId")
//	private String transactionId;
//	@JsonProperty("TransactionDate")
//	private Date transactionDate;
//	@JsonProperty("ResSignature")
//	private String resSignature;
//	@JsonProperty("HitCount")
//	private Double hitCount;

	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("BranchName")
	private String branchName;


	@JsonProperty("PaymentCashRes")
	private PaymentCashRes paymentCashRes;

	@JsonProperty("PaymentChequeRes")
	private PaymentChequeRes paymentChequeRes;

	@JsonProperty("PaymentCreditRes")
	private PaymentCreditRes paymentCreditRes;

	@JsonProperty("PaymentOnlineRes")
	private PaymentOnlineRes paymentOnlineRes;

//	@JsonProperty("PaymentEmiRes")
//	private PaymentEmiRes paymentEmiRes;

}
