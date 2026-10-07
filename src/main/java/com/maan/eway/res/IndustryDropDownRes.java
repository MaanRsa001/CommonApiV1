package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IndustryDropDownRes {

	 
	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("CategoryId")
	private String categoryId;
	@JsonProperty("CategoryDesc")
	private String categoryDesc;
	@JsonProperty("TitleType")
	private String titletype;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
}
