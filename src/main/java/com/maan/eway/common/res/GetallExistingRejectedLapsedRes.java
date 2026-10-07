package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallExistingRejectedLapsedRes {

	   @JsonProperty("TotalCount")
	   private Long     totalCount ;
	   
	   @JsonProperty("CustomerDetails")
	   private List<EserviceCustomerDetailsRes>     customerDetailsRes ;
}
