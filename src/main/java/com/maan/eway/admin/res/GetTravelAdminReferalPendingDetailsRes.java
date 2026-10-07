package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetTravelAdminReferalPendingDetailsRes {
	
		@JsonProperty("Count")
		private Long   count ;

		@JsonProperty("ReferalGridCriteriaAdmin")
	    private List<ReferalGridCriteriaAdminRes>   referalGridCriteriaAdminRes ;
}
