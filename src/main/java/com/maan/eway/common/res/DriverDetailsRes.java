package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DriverDetailsRes {


	@JsonProperty("DriverId")
	private String driverId;
	
	@JsonProperty("DriverName")
	private String driverName;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("DriverDob")
	private Date driverDob;
	
	@JsonProperty("DriverType")
	private String driverType;
	
	@JsonProperty("LicenseNo")
	private String licenseNo;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	

    @JsonProperty("ClaimExperience")
	private Integer claimExperience;
}
