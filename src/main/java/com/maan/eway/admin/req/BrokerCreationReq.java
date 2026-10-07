package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCreationReq {

	@JsonProperty("LoginInformation")
    private BrokerLoginInfoReq loginInformation;
	
	@JsonProperty("PersonalInformation")
    private BrokerPersonalInfoReq personalInformation;
	
}
