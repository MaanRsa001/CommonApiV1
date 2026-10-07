package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachCompaniesReq {

	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("BrokerCompanyYn")
	private String brokerCompanyYn;
	
	
	@JsonProperty("AttachedCompanies")
	private List<AttachedBranchesReq> attachedCompanies ;
}
