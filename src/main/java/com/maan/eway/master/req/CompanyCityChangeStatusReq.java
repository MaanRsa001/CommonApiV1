package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyCityChangeStatusReq {

	 @JsonProperty("CityId")
	 private String cityId;
	 
	 @JsonProperty("Status")
	 private String status;
	 
	 @JsonProperty("InsuranceId")
	 private String companyId;
	 
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
}
