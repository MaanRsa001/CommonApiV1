package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallReferralApprovedDetailsRes {

	@JsonProperty("TotalCount")
	private String totalCount;


	
	
	@JsonProperty("CustomerDetailsRes")
	private List<EserviceCustomerDetailsRes> custRes;
	
}
