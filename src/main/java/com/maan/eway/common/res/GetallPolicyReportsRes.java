package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallPolicyReportsRes {
	
	   @JsonProperty("LoginId")
	   private String     loginId;
	   @JsonProperty("QuoteNo")
	   private String     quoteNo ;
	   @JsonProperty("PolicyNo")
	   private String     policyNo ;
	   @JsonProperty("CustomerName")
	   private String     customerName ;
	   @JsonProperty("StartDate")
	   private String     startDate;
	   @JsonProperty("EndDate")
	   private String     endDate;
	   @JsonProperty("IssueDate")
	   private String     issueDate;
	   @JsonProperty("BranchName")
	   private String     branchName ;
	   @JsonProperty("BrokerName")
	   private String     brokerName ;
	   @JsonProperty("UserType")
	   private String     userType ;
	   @JsonProperty("SubUserType")
	   private String     subUserType;
	   @JsonProperty("Currency")
	   private String     currency;
	   @JsonProperty("PaymentType")
	   private String     paymentType;
	   @JsonProperty("PaymentId")
	   private String     paymentId ;
	   @JsonProperty("PolicyTypeDesc")
	   private String     policyTypeDesc ;
	   @JsonProperty("DebitNoteNo")
	   private String     debitNoteNo ;
	   @JsonProperty("SumInsured")
	   private String     sumInsured;
	   @JsonProperty("Premium")
	   private String     premium;
	   @JsonProperty("CommissionPercentage")
	   private String     commissionPercentage ;
	   @JsonProperty("CommissionAmount")
	   private String     commissionAmount ;
	   
	   @JsonProperty("PassengerCount")
	   private String     passengerCount ;
	  
}
