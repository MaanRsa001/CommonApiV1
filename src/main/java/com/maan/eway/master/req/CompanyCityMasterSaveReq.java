package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyCityMasterSaveReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("CityId")
	private String cityId;

	@JsonProperty("CityName")
	private String cityName;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("StateId")
	private String stateId;

	@JsonProperty("CountryId")
	private String countryId;


	@JsonProperty("Status")
	private String status;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("InsuranceId")
	private String companyId;

}
