package com.maan.eway.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.res.CustomerDetailsGetRes;

import lombok.Data;

@Data
public class GetCustomerRes {

	@JsonProperty("PersonalInfos")
	private CustomerDetailsGetRes customerDetailsGetRes;
}
