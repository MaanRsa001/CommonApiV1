package com.maan.eway.admin.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ModuleIdsRes {

	@JsonProperty("ModuleId")
	private Integer moduleId;

	@JsonProperty("Usertype")
	private String usertype;

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("title")
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

	@JsonProperty("EntryDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private Date entryDate;

	@JsonProperty("id")
	private String moduleNameLocal;

	@JsonProperty("ModuleRemarks")
	private String moduleRemarks;
	
	@JsonProperty("iconBg")
	private String moduleLOGO;
	
	@JsonProperty("route")
	private String moduleURL;

}
