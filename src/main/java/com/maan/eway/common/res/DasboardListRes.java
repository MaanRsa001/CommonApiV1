package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DasboardListRes {

	@JsonProperty("PolicyList")
    private List<DasboardPolicyListRes>     policyList     ;
	@JsonProperty("QuoteList")
    private List<DasboardPolicyListRes>     quoteList     ;
	@JsonProperty("ReferalApproval")
    private List<DasboardPolicyListRes>     raList     ;
		
}
