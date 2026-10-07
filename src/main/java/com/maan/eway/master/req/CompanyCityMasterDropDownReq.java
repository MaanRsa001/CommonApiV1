package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyCityMasterDropDownReq implements Serializable {

    private static final long serialVersionUID = 1L;

	
	@JsonProperty("StateId")
    private String     stateId     ;
    
	@JsonProperty("CountryId")
    private String     countryId     ;
    
	@JsonProperty("InsuranceId")
    private String    companyId     ;
}
