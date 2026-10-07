package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyStateMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("StateId")
	private String stateId;

	@JsonProperty("StateName")
	private String stateName;

	@JsonProperty("StateShortCode")
	private String stateShortCode;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("CountryId")
	private String countryId;

	@JsonProperty("RegionCode")
	private String regionCode;
 
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;

}
