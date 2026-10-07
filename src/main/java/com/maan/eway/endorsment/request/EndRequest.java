package com.maan.eway.endorsment.request;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndRequest {

	@JsonProperty("PolicyNo")
	private String policyNo;
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	@JsonProperty("QuoteNo")
	private String quoteNo;
	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("ProductId")
	private BigDecimal productId;
	@JsonProperty("OriginalPolicyNo")
	private String originalPolicyNo;
	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("EndorsementDate") // EndorsementDate
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date endorsementDate;
	@JsonProperty("EndorsementRemarks") // EndorsementRemarks
	private String endorsementRemarks;
	@JsonProperty("EndorsementEffectiveDate") // EndorsementEffectiveDate
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date endorsementEffdate;
	@JsonProperty("EndtPrevPolicyNo") // EndtPrevPolicyNo
	private String endtPrevPolicyNo;
	@JsonProperty("EndtPrevQuoteNo") // EndtPrevQuoteNo
	private String endtPrevQuoteNo;
	@JsonProperty("EndtCount") // EndtCount
	private BigDecimal endtCount;
	@JsonProperty("EndtStatus") // EndtStatus
	private String endtStatus;
	@JsonProperty("IsFinanceEndt") // IsFinanceEndt
	private String isFinaceYn;
	@JsonProperty("EndtCategoryDesc") // EndtCategoryDesc
	private String endtCategDesc;
	@JsonProperty("EndorsementType") // EndorsementType
	private Integer endorsementType;
	@JsonProperty("EndorsementTypeDesc") // EndorsementTypeDesc
	private String endorsementTypeDesc;
	@JsonProperty("EndorsementYn")
	private String endorsementYn;

}
