package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrokerCommissionDetailsMasterGetRes {

	@JsonProperty("Id")
	private String id;

	@JsonProperty("OaCode")
	private String oaCode;

	@JsonProperty("AgencyCode")
	private String agencyCode;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("PolicyType")
	private String policyType;

	@JsonProperty("PolicyTypeDesc")
	private String policyTypeDesc;

	@JsonProperty("SuminsuredStart")
	private String suminsuredStart;

	@JsonProperty("SuminsuredEnd")
	private String suminsuredEnd;

	@JsonProperty("CommissionPercentage")
	private String commissionPercentage;


	@JsonProperty("CheckerYn")
	private String checkerYn;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("CreatedBy")
	private String createdBy;

	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;

	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonProperty("AmendId")
	private String amendId;

}

