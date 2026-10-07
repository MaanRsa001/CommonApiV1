package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerDatailsGetRes {

	@JsonProperty("LoginInformation")
	private IssuerLoginGetRes loginInformation;

	@JsonProperty("PersonalInformation")
	private IssuerPersonalInfoGetRes personalInformation;

	@JsonProperty("ModuleIds")
	private List<String> moduleIds;

}
