package com.maan.eway.overalldiscount;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.req.UnderwriterAdjustmentReq;

import lombok.Data;

@Data
	
public class FleetDetailsRes {
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("LocationId")
	private String locationId;

	@JsonProperty("PdRefNo")
	private String pdrefno;
	
	@JsonProperty("NoOfVehicles")
	private Integer noOfVehicles;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("VdRefNo")
	private String vdRefNo;
	
	@JsonProperty("PDRefNo")
	private String pdrefno2;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("VehicleId")
	private String vehicleId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	

	@JsonProperty("CdRefNo")
	private String cdRefNo;


	@JsonProperty("MSRefNo")
	private String mSRefNo;
	
	@JsonProperty("EffectiveStartDate")
	private Date effectiveStartDate;

	@JsonProperty("EffectiveEndDate")
	private Date effectiveEndDate;
	
	@JsonProperty("CoverModification")
	private String coverModification;
	
	@JsonProperty("UnderwriterAdjustments")
    private List<UnderwriterAdjustmentReq> underwriterAdjustments;


}
