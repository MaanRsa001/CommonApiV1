package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalPendingResponse {
	
	@JsonProperty("TotalCount")
	private int     totalCount ;
	   
	@JsonProperty("RenewalPolicyList")
	private List<RenewalDetailRes>    renewalDetailRes ;
}
