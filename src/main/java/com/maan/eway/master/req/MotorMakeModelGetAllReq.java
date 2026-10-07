package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotorMakeModelGetAllReq {
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("MakeId")
	private String makeId;
	
	@JsonProperty("Limit")
	private String limit;
	
	@JsonProperty("Offset")
	private String offset;
	
	@JsonProperty("BodyId")
	private String bodyId;
}
