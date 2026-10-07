package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RevertGridRes {

	   @JsonProperty("Count")
	   private Long     count;
	 
	   
	   @JsonProperty("PendingList")
	   private List<RevertGridListRes> pendingList; 
	   
}
