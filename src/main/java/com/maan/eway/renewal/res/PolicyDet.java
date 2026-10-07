package com.maan.eway.renewal.res;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.RenewPremiaPolicy;

import lombok.Data;

@Data
public  class PolicyDet {
	
	    @JsonProperty("TransactionId")
	    private String transactionId;

	    @JsonProperty("PolicyNumber")
	    private String policyNumber;

	    @JsonProperty("PolicyEndDate")
	    private String expiryDate;

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

//	    @JsonProperty("DepartmentCode")
//	    private String departmentCode;
//
//	    @JsonProperty("DepartmentName")
//	    private String departmentName;

	    @JsonProperty("BusinessType")
	    private String businessType;

	    @JsonProperty("BusinessName")
	    private String businessName;

	    @JsonProperty("EndorsementNumber")
	    private String endorsementNumber;

	    @JsonProperty("FromDate")
	    private String fromDate;

	    @JsonProperty("RenewalDate")
	    private String renewalDate;

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

//	    @JsonProperty("PolAssrCode")
//	    private String polAssrCode;
//
//	    @JsonProperty("PolAssrName")
//	    private String polAssrName;

	    @JsonProperty("SourceType")
	    private String polSrcType;

	    @JsonProperty("SourceCode")
	    private String polSrcCode;

	    @JsonProperty("SourceName")
	    private String polSrcName;

	    @JsonProperty("PolicySumInsured")
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
	    private String entryDate;
	    
	    @JsonProperty("PaymentType")
	    private String paymentType;
	    
	    public PolicyDet() {
	    	
	    }


	    public PolicyDet(RenewPremiaPolicy entity) {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	        this.transactionId = entity.getTransactionId();
	        this.policyNumber = entity.getPolicyNumber();
	        this.expiryDate = formatTimestamp(entity.getExpiryDate());
	        this.companyId = entity.getCompanyId();
	        this.companyCode = entity.getCompanyCode();
	        this.companyName = entity.getCompanyName();
	        this.classCode = entity.getClassCode();
	        this.className = entity.getClassName();
	        this.productCode = entity.getProductCode();
	        this.productName = entity.getProductName();
	        this.divisionCode = entity.getDivisionCode();
	        this.divisionName = entity.getDivisionName();
//	        this.departmentCode = entity.getDepartmentCode();
//	        this.departmentName = entity.getDepartmentName();
	        this.businessType = entity.getBusinessType();
	        this.businessName = entity.getBusinessName();
	        this.endorsementNumber = entity.getEndorsementNumber();
	        this.fromDate = formatTimestamp(entity.getFromDate());
	        this.renewalDate = formatTimestamp(entity.getRenewalDate());
	        this.customerCode = entity.getCustomerCode();
	        this.customerName = entity.getCustomerName();
	        this.insuredCivilId = entity.getInsuredCivilId();
	        this.insuredMobile = entity.getInsuredMobile();
	        this.insuredEmailId = entity.getInsuredEmailId();
//	        this.polAssrCode = entity.getPolAssrCode();
//	        this.polAssrName = entity.getPolAssrName();
	        this.polSrcType = entity.getPolSrcType();
	        this.polSrcCode = entity.getPolSrcCode();
	        this.polSrcName = entity.getPolSrcName();
	        this.policySi = entity.getPolicySi();
	        this.grossPremium = entity.getGrossPremium();
	        this.coverPremium = entity.getCoverPremium();
	        this.discountPremium = entity.getDiscountPremium();
	        this.loadingPremium = entity.getLoadingPremium();
	        this.pvtCoverYn = entity.getPvtCoverYn();
	        this.pvtCoverSi = entity.getPvtCoverSi();
	        this.pvtCoverPremium = entity.getPvtCoverPremium();
	        this.chargeAmount = entity.getChargeAmount();
	        this.totalPremium = entity.getTotalPremium();
	        this.agBrokCommission = entity.getAgBrokCommission();
	        this.currentStatus = entity.getCurrentStatus();
	        this.newPolicyNumber = entity.getNewPolicyNumber();
	        this.lossReason = entity.getLossReason();
	        this.lossRemarks = entity.getLossRemarks();
	        this.competitor = entity.getCompetitor();
	        this.entryDate = formatDate(entity.getEntryDate());
	        this.paymentType = entity.getPaymentType();
	    }

	    private String formatTimestamp(Timestamp timestamp) {
	        return (timestamp != null) ? timestamp.toLocalDateTime().toLocalDate().toString() : null;
	    }

	    private String formatDate(Date date) {
	        return (date != null) ? new SimpleDateFormat("yyyy-MM-dd").format(date) : null;
	    }
}




