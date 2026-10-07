package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallReferralRejectedDetailsRes {
	
	@JsonProperty("TotalCount")
	private String totalCount;

//	@JsonProperty("TotalQuoteCount")
//	private String totalQuoteCount;
//	@JsonProperty("TotalEndorsementCount")
//	private String totalEndorsementCount;
//	
	@JsonProperty("CustomerDetailsRes")
	private List<EserviceCustomerDetailsRes> custRes;
}
