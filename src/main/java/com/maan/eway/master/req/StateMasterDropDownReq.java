package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StateMasterDropDownReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("CountryId")
    private String    countryId     ;

	@JsonProperty("RegionCode")
    private String    regionCode;
	
	@JsonProperty("StateId")
    private String    stateId;
}
