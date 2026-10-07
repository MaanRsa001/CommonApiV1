package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ModuleIdsInsReq {
	
	@JsonProperty("ModuleId")
	private Integer moduleId;

	@JsonProperty("Usertype")
	private String usertype;

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("ModuleName")
	private String moduleName;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("DisplayOrder")
	private Integer displayOrder;

	@JsonProperty("DisplayYn")
	private String displayYn;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("ModuleNameLocal")
	private String moduleNameLocal;

	@JsonProperty("ModuleRemarks")
	private String moduleRemarks;
	
	@JsonProperty("ModuleLOGO")
	private String moduleLOGO;
	
	@JsonProperty("ModuleURL")
	private String moduleURL;



}
