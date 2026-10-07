package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CountryMasterRes implements Serializable {

    private static final long serialVersionUID = 1L;


	@JsonProperty("CountryId")
	private String countryId;
	
	@JsonProperty("CountryShortCode")
	private String countryShortCode;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("CountryName")
	private String countryName;

	@JsonProperty("MobileCode")
	private String mobileCode;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("AmendId")
	private Integer amendId;

	@JsonProperty("Remarks")
	private String remarks;
	

	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonProperty("Nationality")
	private String nationality;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
}
