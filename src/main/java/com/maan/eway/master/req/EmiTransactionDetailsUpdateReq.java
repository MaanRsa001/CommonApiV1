package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmiTransactionDetailsUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("NoOfInstallment")
	private String noOfInstallment;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("PaymentStatus")
	private String paymentStatus;
	
	@JsonProperty("PaymentDetails")
	private String paymentDetails;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("SelectedYn")
	private String selectedYn;
	
//	@JsonProperty("PolicyType")
//	private String policyType;
	//
//	@JsonProperty("Status")
//	private String status;
	
	@JsonProperty("InstallmentTypeId")
	private String installmentTypeId;
	    
    @JsonProperty("InstallmentTypeDesc")
    private String installmentTypeDesc;
}
