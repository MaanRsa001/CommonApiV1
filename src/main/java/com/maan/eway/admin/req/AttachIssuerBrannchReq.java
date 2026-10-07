package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachIssuerBrannchReq {


	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("AttachedCompanies")
	private List<AttacheIssuerBranchReq> attachedCompanies ;
}
