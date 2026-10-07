package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportCompanyInfoRes {
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("CompanyLogo")
	private String companyLogo; 
	
	@JsonProperty("CompanySignature")
	private String companySignature ;
	
	@JsonProperty("CompanyWebsite")
	private String companyWebsite;
	
	@JsonProperty("CompanyMail")
	private String companyMail;
	
	@JsonProperty("CompanyPhone")
	private String companyPhone;
	
	@JsonProperty("CompanyAddress")
	private String companyAddress;
	
	@JsonProperty("CompanyPoBox")
	private String companyPoBox;

	 
}
