package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCompanyProductReq {

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("EffectiveDateEnd")
//	private Date effectiveDateEnd;


	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("PaymentYn")
	private String paymentYn;

	@JsonProperty("PaymentRedirUrl")
	private String paymentRedirUrl;

	@JsonProperty("AppLoginUrl")
	private String appLoginUrl;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("ProductDesc")
	private String productDesc;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CreatedBy")
	private String createdBy;

	
	@JsonProperty("FinanceIds")
	private List<String> financeIds;

	@JsonProperty("NonFinanceIds")
	private List<String> nonFinanceIds;
	
	@JsonProperty("CheckerYn")
	private String checkerYn;
	
	@JsonProperty("MakerYn")
	private String makerYn;
	
	@JsonProperty("CustConfirmYn")
	private String custConfirmYn;

//	@JsonProperty("PolicyTypeDesc")
//	private String policyTypeDesc;
	
	@JsonProperty("BrokerCommissionDetails")
	private List<BrokerCommissionDetailsReq> brokerCommissionDetails;
	
	@JsonProperty("OpenCoverNo")
	private String openCoverNo;
	
	@JsonProperty("minimumPremium")
	private String minimumPremium;
	
	

}
