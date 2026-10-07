package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiDocListRes {

	@JsonProperty("FileCode")
	private String fileCode;
	
	@JsonProperty("FileName")
	private String fileName;
	
	@JsonProperty("FileType")
	private String fileType;
	
	@JsonProperty("FilePath")
	private String filePath;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("HasError")
	private String hasError;
	
}
