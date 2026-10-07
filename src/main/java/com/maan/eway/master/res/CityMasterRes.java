package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CityMasterRes implements Serializable {

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
	
//	@JsonProperty("RegionId")
//	private String regionId;
//	
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("AmendId")
	private String amendId;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;


	@JsonProperty("CoreAppCode")
	private String coreAppCode;

}
