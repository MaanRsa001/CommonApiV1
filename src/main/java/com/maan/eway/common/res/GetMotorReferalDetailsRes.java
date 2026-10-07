package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.admin.res.MotorGridCriteriaRes;

import lombok.Data;

@Data
public class GetMotorReferalDetailsRes {

	@JsonProperty("MotorGridCriteria")
	private List<MotorGridCriteriaRes> motorGridCriteriaResRes;
	
	@JsonProperty("TotalCount")
	private Integer totalCount;
	
//	
//	@JsonProperty("TotalQuoteCount")
//	private Integer totalQuoteCount;
//	@JsonProperty("TotalEndorsementCount")
//	private Integer totalEndorsementCount;

}
