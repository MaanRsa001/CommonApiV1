package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;

import lombok.Data;

@Data
public class GetallReferralPendingDetailsRes {

	@JsonProperty("TotalCount")
	private String totalCount;
	

	
	@JsonProperty("CustomerDetailsRes")
	private List<EserviceCustomerDetailsRes> custRes;
}
