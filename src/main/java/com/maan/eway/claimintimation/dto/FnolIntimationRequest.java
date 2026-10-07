package com.maan.eway.claimintimation.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FnolIntimationRequest {

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("IntimationNo")
	private String intimationNo;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("ClaimType")
	private String claimType;

	@JsonProperty("ClaimCategory")
	private String claimCategory;

	@JsonProperty("PartyType")
	private String partyType;

	@JsonProperty("AtFault")
	private String atFault;

	@JsonProperty("LossDate")
	private Date lossDate;

	@JsonProperty("LossLocation")
	private String lossLocation;

	@JsonProperty("LossDesc")
	private String lossDesc;

	@JsonProperty("Code")
	private String code;

	@JsonProperty("ContactPersonMobileNo")
	private Long contactPersonMobileNo;

	@JsonProperty("PoliceStation")
	private String policeStation;

	@JsonProperty("PoliceReportNo")
	private String policeReportNo;

	@JsonProperty("AccidentNo")
	private Integer accidentNo;

	@JsonProperty("ThirdPartyInvolved")
	private String thirdPartyInvolved;
	
	@JsonProperty("PolicyNo")
	private String policyNo;

	@JsonProperty("ThirdPartyList")
	private List<ThirdPartyInfoRequest> thirdPartyList;

	@JsonProperty("DocumentList")
	private List<DocumentInfoRequest> documentList;
}
