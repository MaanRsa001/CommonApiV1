package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.admin.res.ReferalCommonCriteriaRes;

import lombok.Data;

@Data
public class GetCommonReferalDetailsRes {
	
	@JsonProperty("ReferalCommonCriteria")
	private List<ReferalCommonCriteriaRes>  referalCommonCriteriaRes;
	@JsonProperty("TotalCount")
	private Integer totalCount;
	
}
