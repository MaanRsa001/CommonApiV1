package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyStateMultiInsertReq {

	  
		@JsonProperty("StateId")
		private String stateId;

		@JsonProperty("RegionCode")
		private String regionCode;
		
		@JsonProperty("InsuranceId")
		private String companyId;

		@JsonProperty("CreatedBy")
		private String createdBy;
}
