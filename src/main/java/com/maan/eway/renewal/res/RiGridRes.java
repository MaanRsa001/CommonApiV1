package com.maan.eway.renewal.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RiGridRes {

	@JsonProperty("ClientName")
	private String clientName;
	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("QuoteNo")
	private String quoteNo;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("ProductId")
	private Integer  productId;
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	@JsonProperty("CustomerId")
	private String customerId;
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("ReferalRemarks")
	private String referalRemarks;
	@JsonProperty("EndorsementRemarks")
	private String endorsementRemarks;
	@JsonProperty("EndorsementType")
	private String endorsementType;
	@JsonProperty("EndorsementEffdate")
	private Date endorsementEffdate;
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	@JsonProperty("EndtPrevPolicyNo")
	private String endtPrevPolicyNo;
	@JsonProperty("EndtPrevQuoteNo")
	private String endtPrevQuoteNo;
	@JsonProperty("EndtCount")
	private Integer endtCount;
	@JsonProperty("EndtStatus")
	private String endtStatus;
	@JsonProperty("EndtCategDesc")
	private String endtCategDesc;
	@JsonProperty("EndtPremium")
	private BigDecimal endtPremium;
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;

}
