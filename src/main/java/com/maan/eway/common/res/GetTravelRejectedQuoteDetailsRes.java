package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetTravelRejectedQuoteDetailsRes {
	
	   @JsonProperty("TotalCount")
	   private Long     totalCount ;
	   
	   @JsonProperty("TravelRejectCriteriaRes")
	   private List<TravelRejectCriteriaRes> quoteRes;
}
