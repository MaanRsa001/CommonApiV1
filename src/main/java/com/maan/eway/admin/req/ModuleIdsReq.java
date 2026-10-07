package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ModuleIdsReq {
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("ModuleIds")
	private List<String> moduleIds;

}
