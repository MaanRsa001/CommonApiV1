package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CountryMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("CountryId")
	private String countryId;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("CountryName")
	private String countryName;
	
	@JsonProperty("CountryShortCode")
	private String countryShortCode;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("MobileCode")
	private String mobileCode;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("Nationality")
	private String nationality;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
}
