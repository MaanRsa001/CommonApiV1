package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetRejectedQuoteDetailsRes {
	
	   @JsonProperty("TotalCount")
	   private Long     totalCount ;
	   
	   @JsonProperty("QuoteCriteriaRes")
	   private List<RejectCriteriaRes> quoteRes;
}
