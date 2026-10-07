package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoDto {

	@JsonProperty("CompanyName")
    private String companyName;
	@JsonProperty("CompanyId")
    private String companyId;
	@JsonProperty("CompanyEmail")
    private String companyEmail;
	@JsonProperty("CompanyPhone")
    private String companyPhone;
	@JsonProperty("CompanyAddress")
    private String companyAddress;
	@JsonProperty("Signature")
    private String signature;
	@JsonProperty("TinNumber")
    private String tinNumber;
	@JsonProperty("footerImage")
    private String footerImage;
	@JsonProperty("CompanyLogo")
    private String companyLogo;
	@JsonProperty("FooterDescription")
    private String footerDescription;
	@JsonProperty("CompanyWebSite")
    private String companyWebSite;
	@JsonProperty("ApprovedSign")
    private String approvedSign;

}
