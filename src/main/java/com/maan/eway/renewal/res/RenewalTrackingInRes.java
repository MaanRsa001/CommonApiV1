package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackingInRes {

	@JsonProperty("ApproverLoginId")
	private String approverLoginId;
	
	@JsonProperty("BranchList")
	private List<RenewalTrackBranchRes> branchList;
}
