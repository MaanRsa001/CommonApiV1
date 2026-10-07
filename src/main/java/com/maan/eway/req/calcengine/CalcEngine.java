package com.maan.eway.req.calcengine;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.req.UnderwriterAdjustmentReq;

import lombok.Data;

@Data
public class CalcEngine {

	@JsonProperty("InsuranceId")
	private String insuranceId;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("AgencyCode")
	private String agencyCode;

	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("MSRefNo")
	private String msrefno;
	@JsonProperty("VehicleId")
	private String vehicleId;

	@JsonProperty("CdRefNo")
	private String cdRefNo;

	@JsonProperty("VdRefNo")
	private String vdRefNo;
	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	@JsonProperty("MsVehicleDetails")
	private MsVehicleDetails msVehicleDetails;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;

	@JsonProperty("CoverModification")
	private String coverModification;

	@JsonProperty("PDRefNo")
	private String pdrefno;

	@JsonProperty("DdRefNo")
	private String ddRefno;

	@JsonProperty("LocationId")
	private String locationId;

	@JsonProperty("CoverId")
	private String coverId;

	@JsonProperty("UnderwriterAdjustments")
	private List<UnderwriterAdjustmentReq> underwriterAdjustments;

	@JsonProperty("IsReferral")
	private String isReferral;

	@JsonProperty("ReferralRemarks")
	private String referralRemarks;
	
	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	
	@JsonProperty("EndtOpdt")
	private String endtOpdt;
	
//	@JsonProperty("CoverItems")
//	private List<CoverItem> coverItems;

}
