package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RevertGridListRes {

	
	   
	   @JsonProperty("UnderWriterLoginId")
	   private String     underWriterLoginId;
	   
	   @JsonProperty("ProductId")
	   private String     productId;
	   
	   @JsonProperty("RequestReferenceNo")
	   private String     requestReferenceNo;
	   
	   @JsonProperty("BranchCode")
	   private String     BranchCode; 
	   
	   @JsonProperty("Status")
	   private String     status;
	   
	   @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("EntryDate")
	   private String     EntryDate; 
	   
	   @JsonProperty("UWStatus")
	   private String     uwStatus ;

	   
//	   @JsonProperty("BrokerName")
//	   private String     brokerName;
//	   
//	   @JsonProperty("CustomerName")
//	   private String     customerName;
//	   
//	   @JsonProperty("BrokerCode")
//	   private String     brokerCode;
//	   
//	   
//	   @JsonProperty("UserType")
//	   private String     userType;
//	   
//	   @JsonProperty("SubUserType")
//	   private String     subUserType;
	   
//	   @JsonProperty("OriginalPolicyNo")
//	   private String     originalPolicyNo;
//	   
//	   @JsonProperty("PolicyStartDate")
//	   private String     policyStartDate;
//	   
//	   @JsonProperty("PolicyEndDate")
//	   private String     policyEndDate;
//	   
//	   
//	   @JsonProperty("ProductName")
//	   private String     productName;
//	  
//	   @JsonProperty("AdminRemarks")
//	   private String     adminRemarks;
//	   
//	   @JsonProperty("ReferralRemarks")
//	   private String     referralRemarks;
//	   
//	   @JsonProperty("Remarks")
//	   private String     remarks;
//	   
//	   @JsonProperty("AdminLoginId")
//	   private String     adminLoginId;
	   
	
	   
//	   @JsonProperty("EndtSatus")
//	   private String     endtSatus;
//	   
//	   @JsonProperty("StatusDesc")
//	   private String     statusDesc;
//	   
//	   @JsonProperty("EndtStatusDesc")
//	   private String     endtStatusDesc;
//	   
	
	   
//	   @JsonProperty("BranchName")
//	   private String     branchName; 
//	   
//	   @JsonProperty("BrokerBranchCode")
//	   private String     brokerBranchCode; 
//	   
//	   @JsonProperty("BrokerBranchName")
//	   private String     brokerBranchName; 
//	
//	   @JsonProperty("UpdatedDate")
//	   private String     updatedDate; 
//	   
//	   @JsonProperty("EndorsementRemarks")
//	   private String     endorsementRemarks; 
}
