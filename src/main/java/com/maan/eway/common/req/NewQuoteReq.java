package com.maan.eway.common.req;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.FactorRateRequestDetails;

import lombok.Data;

@Data
public class NewQuoteReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("Vehicles")
	private List<VehicleIdsReq> vehicleIdsList;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("AdminLoginId")
	private String adminLoginId;

	@JsonProperty("ManualReferralYn")
	private String manualReferralYn;

	@JsonProperty("ReferralRemarks")
	private String referralRemarks;

	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("MotorYn")
	private String motorYn;

	@JsonProperty("CommissionModifyYn")
	private String commissionModifyYn;

	@JsonProperty("CommissionPercent")
	private String commissionPercent;

	@JsonProperty("EmiYn")
	private String emiYn;
	
	@JsonProperty("Entd")
	private boolean entd;

	@JsonProperty("WhatsappPolicy")
	private String whatsappPolicy;

	private Map<String, List<EserviceMotorDetails>> motorDetailsMap = new HashMap<>();

	public Map<String, List<EserviceMotorDetails>> getMotorDetailsMap() {
		if (motorDetailsMap == null) {
			motorDetailsMap = new HashMap<>();
		}
		return motorDetailsMap;
	}

	private List<EserviceCommonDetails> humanDatas ;
	
	private List<EserviceBuildingDetails> buildDatas;
	
	private EserviceTravelDetails travelDatas;
	
	private CompanyProductMaster product;
	
	private List<EserviceSectionDetails> getSectionDatas;
	
	private EserviceCustomerDetails custData;
	
	private String companyid;
	
	private List<FactorRateRequestDetails> covers;
	
	
}
