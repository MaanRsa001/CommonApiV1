package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotorVehicleUsageMasterGetallReq {
	
//
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("EffectiveDateStart")
//	private Date effectiveDateStart;
	@JsonProperty("SectionId")
    private String sectionId  ;
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
}
