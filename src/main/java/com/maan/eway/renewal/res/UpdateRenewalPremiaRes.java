package com.maan.eway.renewal.res;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateRenewalPremiaRes {
	@JsonProperty("TransactionId")
	private String transactionId;

	@JsonProperty("PolicyNumber")
	private String policyNumber;

	@JsonProperty("ExpiryDate")
	private Timestamp expiryDate;

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("CompanyCode")
	private String companyCode;

	@JsonProperty("CompanyName")
	private String companyName;

	@JsonProperty("ClassCode")
	private String classCode;

	@JsonProperty("ClassName")
	private String className;

	@JsonProperty("ProductCode")
	private String productCode;

	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("DivisionCode")
	private String divisionCode;

	@JsonProperty("DivisionName")
	private String divisionName;

	@JsonProperty("DepartmentCode")
	private String departmentCode;

	@JsonProperty("DepartmentName")
	private String departmentName;

	@JsonProperty("BusinessType")
	private String businessType;

	@JsonProperty("BusinessName")
	private String businessName;

	@JsonProperty("EndorsementNumber")
	private String endorsementNumber;

	@JsonProperty("FromDate")
	private Timestamp fromDate;

	@JsonProperty("RenewalDate")
	private Timestamp renewalDate;

	@JsonProperty("CustomerCode")
	private String customerCode;

	@JsonProperty("CustomerName")
	private String customerName;

	@JsonProperty("InsuredCivilId")
	private String insuredCivilId;

	@JsonProperty("InsuredMobile")
	private String insuredMobile;

	@JsonProperty("InsuredEmailId")
	private String insuredEmailId;

	@JsonProperty("PolAssrCode")
	private String polAssrCode;

	@JsonProperty("PolAssrName")
	private String polAssrName;

	@JsonProperty("PolSrcType")
	private String polSrcType;

	@JsonProperty("PolSrcCode")
	private String polSrcCode;

	@JsonProperty("PolSrcName")
	private String polSrcName;

	@JsonProperty("PolicySi")
	private Double policySi;

	@JsonProperty("GrossPremium")
	private Double grossPremium;

	@JsonProperty("CoverPremium")
	private Double coverPremium;

	@JsonProperty("DiscountPremium")
	private Double discountPremium;

	@JsonProperty("LoadingPremium")
	private Double loadingPremium;

	@JsonProperty("PvtCoverYn")
	private Integer pvtCoverYn;

	@JsonProperty("PvtCoverSi")
	private Double pvtCoverSi;

	@JsonProperty("PvtCoverPremium")
	private Double pvtCoverPremium;

	@JsonProperty("ChargeAmount")
	private Double chargeAmount;

	@JsonProperty("TotalPremium")
	private Double totalPremium;

	@JsonProperty("AgBrokCommission")
	private Double agBrokCommission;

	@JsonProperty("CurrentStatus")
	private String currentStatus;

	@JsonProperty("NewPolicyNumber")
	private String newPolicyNumber;

	@JsonProperty("LossReason")
	private String lossReason;

	@JsonProperty("LossRemarks")
	private String lossRemarks;

	@JsonProperty("Competitor")
	private String competitor;

	@JsonProperty("EntryDate")
	private Date entryDate;

	@JsonProperty("Status")
	private String status;
}
