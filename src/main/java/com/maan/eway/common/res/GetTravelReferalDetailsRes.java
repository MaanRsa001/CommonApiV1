package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;

import lombok.Data;
@Data
public class GetTravelReferalDetailsRes {
	@JsonProperty("ReferalGridCriteria")
	private List<ReferalGridCriteriaRes>  referalGridCriteriaRes;
	
	@JsonProperty("TotalCount")
	private Integer totalCount;
	
}
