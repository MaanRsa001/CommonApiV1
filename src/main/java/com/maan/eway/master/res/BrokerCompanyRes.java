package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCompanyRes {

	@JsonProperty("InsuranceId")
    private String     companyId     ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
    private Date       effectiveDateStart ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
    private Date       effectiveDateEnd ;
	
	@JsonProperty("CompanyName")
    private String     companyName ;
	
	@JsonProperty("CompanyAddress")
    private String     companyAddress ;
	
	@JsonProperty("CompanyEmail")
    private String     companyEmail ;
	
	@JsonProperty("CompanyPhone")
    private String     companyPhone ;
	
	@JsonProperty("CompanyLogo")
    private String     companyLogo ;
	
	@JsonProperty("Regards")
    private String     regards ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date       entryDate    ;
	
	@JsonProperty("Status")
    private String     status       ;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("BrokerYn")
	private String brokerYn;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
		
}
