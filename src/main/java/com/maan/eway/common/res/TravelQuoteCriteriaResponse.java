package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class TravelQuoteCriteriaResponse {
	
	   @JsonProperty("TotalCount")
	   private Long     totalCount ;
	   
	   @JsonProperty("TravelQuoteCriteriaRes")
	   private List<TravelQuoteCriteriaRes> quoteRes;
}
