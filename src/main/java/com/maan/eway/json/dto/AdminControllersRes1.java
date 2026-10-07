package com.maan.eway.json.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminControllersRes1 {

	@JsonProperty("PackageName")
	private String packageName;
	
	@JsonProperty("ControllerClass")
	private String controllerClass;
	
	@JsonProperty("MethodName")
	private String methodName;
	
	@JsonProperty("MethodType")
	private String methodType;
	
	@JsonProperty("ApiURL")
	private String apiURL;
	
	@JsonProperty("ProjectName")
	private String projectName;
	
}
